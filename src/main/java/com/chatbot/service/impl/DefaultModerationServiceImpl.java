package com.chatbot.service.impl;

import com.chatbot.service.MessageService;
import com.chatbot.service.ModerationService;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.helix.domain.Follow;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultModerationServiceImpl implements ModerationService {

    private static DefaultModerationServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultModerationServiceImpl.class);

    private static final String SUSPICIOUS_KEYWORDS_PATH = "moderation/suspicious-keywords.txt";

    private static final String CHARACTERS_REPLACEMENT_MAP_RU = "moderation/character-replacement-map-ru.txt";

    private static final int MATCH_THRESHOLD_MAX_VALUE = 999;

    private final Set<String> suspiciousKeyWords = new HashSet<>(readDictionary(SUSPICIOUS_KEYWORDS_PATH));

    private final Map<Character, List<String>> charMapRuEn = getCharReplacementMap();

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    private DefaultModerationServiceImpl() {
    }

    public static synchronized DefaultModerationServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultModerationServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean isBotModeratorOnChannel(final String channelName) {
        if (configurationService.getConfiguration(channelName).isCheckModeratorPermissions()) {
            final String botName = configurationService.getBotName().toLowerCase();
            final List<String> channelModerators = twitchClientService.getTwitchClient().getMessagingInterface().getChatters(channelName).execute().getModerators();
            return CollectionUtils.isNotEmpty(channelModerators) && channelModerators.stream().map(String::toLowerCase).collect(Collectors.toSet()).contains(botName);
        }
        return true;
    }

    @Override
    public void banUser(final String channelName, final String userName, final String reason) {
        final String command = String.format(messageService.getStandardMessageForKey("command.moderation.ban"), userName, reason);
        messageService.sendMessage(channelName, messageService.getMessageBuilder().withText(command), null);
    }

    @Override
    public void timeoutUser(final String channelName, final String userName, final String reason, final Integer duration) {
        final String command = String.format(messageService.getStandardMessageForKey("command.moderation.timeout"), userName, duration, reason);
        messageService.sendMessage(channelName, messageService.getMessageBuilder().withText(command), null);
    }

    @Override
    public boolean isSuspiciousMessage(final String channelName, final String message, final Set<CommandPermission> userPermissions) {
        final int matchThreshold = configurationService.getConfiguration(channelName).getModerationWordNumberThreshold();
        return !isWhiteListedUser(channelName, userPermissions) && getSuspiciousWordsMatchCount(message, matchThreshold) >= matchThreshold;
    }

    @Override
    public int getSuspiciousWordsMatchCount(final String message) {
        return getSuspiciousWordsMatchCount(message, null);
    }

    @Override
    public int getSuspiciousWordsMatchCount(final String message, final Integer matchThreshold) {
        final int threshold = matchThreshold != null ? matchThreshold : MATCH_THRESHOLD_MAX_VALUE;
        int matchCounter = 0;
        for (final String keyword : suspiciousKeyWords) {
            final List<String> keywordTokens = Arrays.stream(keyword.split("\\|")).filter(token -> !token.isEmpty()).collect(Collectors.toList());
            if (keywordTokens.stream().anyMatch(wordToken -> contains(message, wordToken))) {
                matchCounter++;
            }
            if (matchCounter >= threshold) {
                break;
            }
        }
        return matchCounter;
    }

    @Override
    public boolean isFirstMessage(final ChannelMessageEvent event) {
        final Map<String, String> messageTags = event.getMessageEvent().getTags();
        return messageTags.containsKey("first-msg") && !messageTags.get("first-msg").equals("0");
    }

    @Override
    public boolean isFollowing(final String userId, final String channelId) {
        if (userId.equalsIgnoreCase(channelId)) {
            return true;
        }
        return getUserFollowOnChannel(userId, channelId).isPresent();
    }

    @Override
    public long getFollowAgeInSeconds(final String userId, final String channelId) {
        if (userId.equalsIgnoreCase(channelId)) {
            return Long.MAX_VALUE;
        }
        final Optional<Follow> followOptional = getUserFollowOnChannel(userId, channelId);
        if (followOptional.isEmpty() || followOptional.get().getFollowedAtInstant() == null) {
            return 0;
        }
        final Instant followInstant = followOptional.get().getFollowedAtInstant();
        return Instant.now().getEpochSecond() - followInstant.getEpochSecond();
    }

    @Override
    public long getUserAgeInSeconds(final String userId) {
        final Optional<User> userOptional = twitchClientService.getTwitchHelixClient().getUsers(getAuthToken(), List.of(userId), null).execute().getUsers().stream().findFirst();
        if (userOptional.isEmpty() || userOptional.get().getCreatedAt() == null) {
            return 0;
        }
        final Instant createdInstant = userOptional.get().getCreatedAt();
        return Instant.now().getEpochSecond() - createdInstant.getEpochSecond();
    }

    private Optional<Follow> getUserFollowOnChannel(final String userId, final String channelId) {
        return twitchClientService.getTwitchHelixClient().getFollowers(getAuthToken(), userId, channelId, null, null).execute().getFollows().stream().findFirst();
    }

    private boolean contains(final String message, final String originalWordToken) {
        final Set<Character> charsetToInspect = originalWordToken.chars().mapToObj(e->(char)e).collect(Collectors.toSet());
        charsetToInspect.retainAll(charMapRuEn.keySet());

        final List<String> wordsToInspect = new ArrayList<>();
        wordsToInspect.add(originalWordToken);
        wordsToInspect.addAll(generateWordsByReplacingChars(originalWordToken, charsetToInspect));

        return wordsToInspect.stream().anyMatch(word -> StringUtils.containsIgnoreCase(message, word));
    }

    private List<String> generateWordsByReplacingChars(final String originalWordToken, final Set<Character> charsetToInspect) {
        final List<Character> wordCharset = originalWordToken.chars().mapToObj(e->(char)e).collect(Collectors.toList());
        if (!CollectionUtils.containsAny(wordCharset, charsetToInspect)) {
            return Collections.emptyList();
        }
        final List<String> additionalWordTokens = new ArrayList<>();

        // replace 1 char
        for (int position = 0; position < wordCharset.size(); position++) {
            if (charsetToInspect.contains(wordCharset.get(position))) {
                for (final String partToReplace : charMapRuEn.get(wordCharset.get(position))) {
                    additionalWordTokens.add(originalWordToken.substring(0, position) + partToReplace + originalWordToken.substring(position + 1));
                }
            }
        }
        // replace all chars
        final StringBuilder sb = new StringBuilder();
        for (final Character character : wordCharset) {
            if (charsetToInspect.contains(character)) {
                sb.append(charMapRuEn.get(character).iterator().next());
            } else {
                sb.append(character);
            }
        }
        additionalWordTokens.add(sb.toString());
        return additionalWordTokens;
    }

    private boolean isWhiteListedUser(final String channelName, final Set<CommandPermission> userPermissions) {
        return CollectionUtils.containsAny(getMessageWhitelistedPermissions(channelName), userPermissions);
    }

    private Set<CommandPermission> getMessageWhitelistedPermissions(final String channelName) {
        return configurationService.getConfiguration(channelName).getMessageWhitelistedPermissions().stream().map(CommandPermission::valueOf).collect(Collectors.toSet());
    }

    private Map<Character, List<String>> getCharReplacementMap() {
        final Map<Character, List<String>> map = new HashMap<>();
        readDictionary(CHARACTERS_REPLACEMENT_MAP_RU).forEach(line -> {
            final String[] lineTokens = line.split("-");
            if (lineTokens.length > 1) {
                map.put(lineTokens[0].charAt(0), List.of(Arrays.copyOfRange(lineTokens, 1, lineTokens.length)));
            }
        });
        return map;
    }

    private Set<String> readDictionary(final String path) {
        final Set<String> keywords = new HashSet<>();
        final URL url = getClass().getClassLoader().getResource(path);
        final File keywordFilePath = url != null ? new File(url.getFile()) : null;
        if (keywordFilePath == null || !keywordFilePath.exists()) {
            return keywords;
        }
        try {
            final Scanner scanner = new Scanner(keywordFilePath);
            scanner.useDelimiter("\r\n");
            while (scanner.hasNext()){
                final String line = scanner.next();
                if (StringUtils.isNotBlank(line) && !line.startsWith("#")) {
                    keywords.add(line);
                }
            }
            scanner.close();
        } catch (final FileNotFoundException e) {
            LOG.error(String.format("Can't read file %s", keywordFilePath));
        }
        return keywords;
    }

    private String getAuthToken() {
        return configurationService.getCredentialProperties().getProperty("twitch.credentials.access.token");
    }

    private String getUserId(final String userName) {
        final UserList userList = twitchClientService.getTwitchHelixClient().getUsers(getAuthToken(), null, List.of(configurationService.getBotName())).execute();
        return userList.getUsers().stream().filter(user -> userName.equalsIgnoreCase(user.getLogin())).map(User::getId).findFirst().orElse(StringUtils.EMPTY);
    }
}
