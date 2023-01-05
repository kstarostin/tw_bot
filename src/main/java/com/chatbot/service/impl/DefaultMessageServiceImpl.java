package com.chatbot.service.impl;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.RandomizerService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.TwitchEmoteService;
import com.chatbot.util.FeatureEnum;
import com.chatbot.service.MessageService;
import com.chatbot.util.emotes.AbstractEmote;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;


public class DefaultMessageServiceImpl implements MessageService {
    private static DefaultMessageServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultMessageServiceImpl.class);

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private static final int MAX_MESSAGE_LENGTH = 450;

    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final RandomizerService randomizerService = DefaultRandomizerServiceImpl.getInstance();
    private final TwitchEmoteService twitchEmoteService = DefaultTwitchEmoteServiceImpl.getInstance();

    private DefaultMessageServiceImpl() {
    }

    public static synchronized DefaultMessageServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultMessageServiceImpl();
        }
        return instance;
    }

    @Override
    public void sendMessage(final String channelName, final MessageBuilder messageBuilder, final ChannelMessageEvent event) {
        sendMessage(channelName, messageBuilder, true, event);
    }

    @Override
    public void sendMessage(final String channelName, final MessageBuilder messageBuilder, final boolean isMuteChecked, final ChannelMessageEvent event) {
        final String responseMessage = messageBuilder.buildForTwitch();
        if (responseMessage.isEmpty()) {
            return;
        }
        if (!configurationService.getTwitchBotName().equalsIgnoreCase(channelName) && !configurationService.getConfiguration().getTwitchChannels().contains(channelName)) {
            return;
        }
        if (isMuteChecked && configurationService.getConfiguration(channelName).isMuted()) {
            return;
        }
        if (botFeatureService.isTwitchFeatureActive(channelName, FeatureEnum.LOGGING)) {
            final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            LOG.info("Channel[{}]-[{}]:[{}]:[{}]", channelName, formatter.format(new Date()) , configurationService.getTwitchBotName(), responseMessage);
        }
        final List<String> messageParts = new ArrayList<>();

        if (responseMessage.length() > MAX_MESSAGE_LENGTH) {
            messageParts.addAll(splitByMaxLength(responseMessage));
        } else {
            messageParts.add(responseMessage);
        }

        messageParts.forEach(part -> {
            if (event != null) {
                event.reply(twitchClientService.getTwitchClient().getChat(), part);
            } else {
                twitchClientService.getTwitchClient().getChat().sendMessage(channelName, part);
            }
        });
    }

    @Override
    public void sendMessageWithDelay(final String channelName, final MessageBuilder messageBuilder, final int delay, final ChannelMessageEvent event) {
        if (configurationService.getConfiguration(channelName).isMuted()) {
            return;
        }
        if (messageBuilder.buildForTwitch().isEmpty()) {
            return;
        }
        if (delay == 0) {
            sendMessage(channelName, messageBuilder, event);
            return;
        }
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        sendMessage(channelName, messageBuilder, event);
                    }
                },
                delay
        );
    }

    @Override
    public String getStandardMessageForKey(final String key) {
        final String propertyMessage = configurationService.getProperties("messages/messages.properties").getProperty(key);
        final String[] messages = StringUtils.isNotEmpty(propertyMessage) ? propertyMessage.split("\\|") : new String[0];
        return messages.length > 0 ? messages[randomizerService.rollDice(messages.length)] : StringUtils.EMPTY;
    }

    @Override
    public String getPersonalizedMessageForKey(final String personalizedKey, final String defaultKey) {
        final String message = getStandardMessageForKey(personalizedKey);
        if (StringUtils.isEmpty(message)) {
            return getStandardMessageForKey(defaultKey);
        }
        return message;
    }

    @Override
    public MessageBuilder getMessageBuilder() {
        return new MessageBuilder();
    }

    @Override
    public MessageSanitizer getMessageSanitizer(final String text) {
        return new MessageSanitizer(text);
    }

    private List<String> splitByMaxLength(final String message) {
        final String[] words = message.split("\\s+");
        if (words.length > 1) {
            final List<StringBuilder> splitMessages = new ArrayList<>();
            StringBuilder shortenedMessage = new StringBuilder();
            splitMessages.add(shortenedMessage);
            for (final String word : words) {
                if (shortenedMessage.length() + word.length() + 1 <= MAX_MESSAGE_LENGTH) {
                    shortenedMessage.append(word).append(StringUtils.SPACE);
                } else if (StringUtils.isNotEmpty(shortenedMessage.toString())) {
                    shortenedMessage = new StringBuilder();
                    splitMessages.add(shortenedMessage);
                }
            }
            return splitMessages.stream().map(sb -> sb.toString().trim()).collect(Collectors.toList());
        } else {
            return List.of(message);
        }
    }

    public static class MessageBuilder {
        private static final String TWITCH_TAG_TEMPLATE = "@%s";
        private static final String DISCORD_TAG_TEMPLATE = "<@%s>";

        private String tag;
        private boolean startsWithTag;
        private String text;
        private List<? extends AbstractEmote> emotes;

        private MessageBuilder() {
        }

        public MessageBuilder withUserTag(final String tag) {
            this.tag = tag;
            this.startsWithTag = true;
            return this;
        }

        public MessageBuilder withUserTag(final String tag, final boolean startsWithTag) {
            this.tag = tag;
            this.startsWithTag = startsWithTag;
            return this;
        }

        public MessageBuilder withText(final String text) {
            this.text = text;
            return this;
        }

        public MessageBuilder withEmotes(final List<? extends AbstractEmote> emotes) {
            this.emotes = emotes;
            return this;
        }

        @Override
        public String toString() {
            return buildForTwitch();
        }

        public String buildForTwitch() {
            return build(TWITCH_TAG_TEMPLATE);
        }

        public String buildForDiscord() {
            return build(DISCORD_TAG_TEMPLATE);
        }

        private String build(final String tagTemplate) {
            final StringBuilder sb = new StringBuilder();
            if (startsWithTag && StringUtils.isNotEmpty(tag)) {
                sb.append(String.format(tagTemplate, tag));
            }
            if (StringUtils.isNotEmpty(text)) {
                sb.append(StringUtils.SPACE).append(text);
            }
            if (!startsWithTag && StringUtils.isNotEmpty(tag)) {
                sb.append(StringUtils.SPACE).append(String.format(tagTemplate, tag));
            }
            if (CollectionUtils.isNotEmpty(emotes)) {
                emotes.forEach(emote -> sb.append(StringUtils.SPACE).append(emote.toString()));
            }
            return sb.toString().trim();
        }

        public boolean isNotEmpty() {
            return StringUtils.isNotEmpty(tag) || StringUtils.isNotEmpty(text) || CollectionUtils.isNotEmpty(emotes);
        }
    }

    public class MessageSanitizer {
        private final String[] DELIMITERS = {".", "?", "!"};
        private static final String TWITCH_TAG_CHARACTER = "@";

        private final String text;
        private boolean removeTags;
        private boolean removeEmotes;
        private Integer maxLength;
        private boolean checkDelimiter;
        private String defaultDelimiter = ".";

        private MessageSanitizer(final String text) {
            this.text = text;
        }

        public MessageSanitizer withNoTags() {
            this.removeTags = true;
            return this;
        }

        public MessageSanitizer withNoEmotes() {
            this.removeEmotes = true;
            return this;
        }

        public MessageSanitizer withDelimiter() {
            this.checkDelimiter = true;
            return this;
        }

        public MessageSanitizer withDelimiter(final String delimiter) {
            this.defaultDelimiter = delimiter;
            this.checkDelimiter = true;
            return this;
        }

        public MessageSanitizer withMaxLength(final int maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        public String sanitizeForTwitch(final String channelId, final String channelName) {
            List<String> sanitizedWords = Arrays.asList(text.trim().split(StringUtils.SPACE));
            if (removeTags) {
                sanitizedWords = removeTags(sanitizedWords, channelName);
            }
            if (removeEmotes) {
                sanitizedWords = removeEmotes(sanitizedWords, channelId);
            }
            if (sanitizedWords.isEmpty()) {
                return StringUtils.EMPTY;
            }
            final String sanitizedMessage = (maxLength != null && maxLength > 0)
                    ? reduceLength(sanitizedWords).trim()
                    : String.join(StringUtils.SPACE, sanitizedWords).trim();
            return checkDelimiter && !StringUtils.endsWithAny(sanitizedMessage, DELIMITERS) ? sanitizedMessage + defaultDelimiter : sanitizedMessage;
        }

        private List<String> removeTags(final List<String> words, final String channelName) {
            return words.stream()
                    .filter(word -> !word.startsWith(TWITCH_TAG_CHARACTER))
                    .filter(word -> !StringUtils.equalsIgnoreCase(word, configurationService.getTwitchBotName()))
                    .filter(word -> !StringUtils.equalsAnyIgnoreCase(word, CollectionUtils.emptyIfNull(configurationService.getConfiguration(channelName).getAdditionalBotTagNames()).toArray(new String[0])))
                    .collect(Collectors.toList());
        }

        private List<String> removeEmotes(final List<String> words, final String channelId) {
            return words.stream()
                    .filter(word -> !twitchEmoteService.isEmote(channelId, word))
                    .collect(Collectors.toList());
        }

        private String reduceLength(final List<String> words) {
            String sanitizedMessage = String.join(StringUtils.SPACE, words);
            final StringBuilder sanitizedMessageBuilder = new StringBuilder();
            for (final String word : words) {
                if (sanitizedMessageBuilder.length() + word.length() + 1 < maxLength) {
                    sanitizedMessageBuilder.append(word).append(StringUtils.SPACE);
                } else if (StringUtils.isNotEmpty(sanitizedMessageBuilder.toString())) {
                    sanitizedMessage = sanitizedMessageBuilder.toString();
                    break;
                } else {
                    sanitizedMessage = (sanitizedMessage.length() > maxLength ? sanitizedMessage.substring(0, maxLength) : sanitizedMessage).trim();
                    break;
                }
            }
            return sanitizedMessage;
        }
    }
}
