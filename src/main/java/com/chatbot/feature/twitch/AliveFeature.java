package com.chatbot.feature.twitch;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.service.PeriodCacheService;
import com.chatbot.service.ModerationService;
import com.chatbot.service.TwitchEmoteService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultPeriodCacheServiceImpl;
import com.chatbot.service.impl.DefaultModerationServiceImpl;
import com.chatbot.service.impl.DefaultTwitchEmoteService;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.chatbot.util.emotes.BotEmote.Sets.*;

public class AliveFeature extends AbstractFeature {
    private static final int RND_TRIGGER_MIN_PROBABILITY = 1;
    private static final int RND_TRIGGER_MAX_PROBABILITY = 100;

    private final static MessageType[] RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP = {
            MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE,
            MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE,
            MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY,
            MessageType.SEND_MESSAGE
    };

    private static final String USERNAME_TOKEN = "${name}";
    private static final String GREETING_TOKEN = "${greeting}";
    private static final String ADDITION_TOKEN = "${addition}";
    private static final Set<String> USER_FRIEND_LIST = Set.of("0mskbird", "yura_atlet", "1skybox1", "chenushka", "hereticjz", "skvdee", "svetloholmov", "prof_133", "kiber_bober",
            "poni_prancing", "greyraise", "panthermania", "tachvnkin", "tesla013");

    private final ResponseGenerator balabobaResponseGenerator = BalabobaResponseGenerator.getInstance();
    private final ModerationService moderationService = DefaultModerationServiceImpl.getInstance();
    private final PeriodCacheService cacheService = DefaultPeriodCacheServiceImpl.getInstance();
    private final TwitchEmoteService twitchEmoteService = DefaultTwitchEmoteService.getInstance();

    public AliveFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String channelId = event.getChannel().getId();
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActive(channelName, FeatureEnum.ALIVE) || (isActiveOnLiveStreamOnly(channelName) && !isStreamLive(event.getChannel().getName()))) {
            return;
        }
        final String message = event.getMessage();
        if (isCommand(message) || moderationService.isSuspiciousMessage(channelName, message, event.getPermissions())) {
            return;
        }
        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder();
        if (isGreetingResponse(channelName, userName, message)) {
            responseMessageBuilder.withText(buildGreetingText(userName));
            if (getRandomIntExponentially(2, 2) == 0) {
                responseMessageBuilder.withEmotes(buildEmotes(channelId, 3, GREETING, POG, HAPPY));
            }
            greetWithDelay(channelName, userName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), event);
        } else if (isEmoteOnlyMessage(channelId, message)) {
            final List<String> emoteSet = getEmoteSet(message);
            if (CollectionUtils.isNotEmpty(emoteSet) && getRandomIntExponentially(2, 2) == 1) {
                responseMessageBuilder.withEmotes(buildEmotes(channelId, 3, emoteSet));
                messageService.sendMessageWithDelay(channelName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), null);
            }
        } else if (isBotTagged(message) || (isNoOneTagged(message) && isRandomBotTrigger(channelName))) {
            responseMessageBuilder.withText(balabobaResponseGenerator.generate(sanitizeRequestMessage(message), true, true, false))
                    .withEmotes(buildEmotes(channelId, 2, CONFUSION, HAPPY));
            if (responseMessageBuilder.isNotEmpty()) {
                sendMessageWithDelay(channelName, userName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), event);
            }
        }
    }

    private boolean isGreetingResponse(final String channelName, final String userName, final String message) {
        return isGreetingEnabled(channelName) && !isUserGreeted(channelName, userName) && (isUserInFriendList(userName) || isRandomGreeting()) && !isBotTagged(message);
    }

    private boolean isGreetingEnabled(final String channelName) {
        return configurationService.getConfiguration(channelName).isUserGreetingEnabled();
    }

    private boolean isUserGreeted(final String channelName, final String userName) {
        return cacheService.getCachedGreetings(channelName).isPresent() && cacheService.getCachedGreetings(channelName).get().contains(userName);
    }

    private boolean isUserInFriendList(final String userName) {
        return USER_FRIEND_LIST.contains(userName.toLowerCase());
    }

    private boolean isRandomGreeting() {
        return getRandomIntExponentially(2, 3) == 1;
    }

    private boolean isBotTagged(final String message) {
        return StringUtils.containsIgnoreCase(message, configurationService.getBotName());
    }

    private boolean isEmoteOnlyMessage(final String channelId, final String message) {
        final String[] messageTokens = message.split(StringUtils.SPACE);
        for (final String token : messageTokens) {
            if (!isEmote(channelId, token)) {
                return false;
            }
        }
        return true;
    }

    private List<String> getEmoteSet(final String emoteText) {
        final String[] emotes = emoteText.trim().split(StringUtils.SPACE);
        for (final String emote : emotes) {
            for (final List<String> set : ALL_SETS) {
                if (set.contains(emote)) {
                    return set;
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean isEmote(final String channelId, final String text) {
        return twitchEmoteService.getValidEmoteNames(channelId).contains(text);
    }

    private boolean isNoOneTagged(final String message) {
        return !message.contains(TAG_CHARACTER);
    }

    private boolean isRandomBotTrigger(final String channelName) {
        final SplittableRandom random = new SplittableRandom();
        return random.nextInt(RND_TRIGGER_MIN_PROBABILITY, RND_TRIGGER_MAX_PROBABILITY + 1) <= configurationService.getConfiguration(channelName).getIndependenceRate();
    }

    private String buildGreetingText(final String userName) {
        final List<String> greetingTokens = Arrays.asList(USERNAME_TOKEN, GREETING_TOKEN, ADDITION_TOKEN);
        Collections.shuffle(greetingTokens);
        final StringBuilder sb = new StringBuilder();
        greetingTokens.forEach(token -> {
            final int exponent = ADDITION_TOKEN.equals(token) ? 3 : 2;
            if (getRandomIntExponentially(2, exponent) == 0) {
                sb.append(StringUtils.SPACE).append(token);
            }
        });
        String messageTemplate = sb.toString();
        for (final String token : greetingTokens) {
            final String replacement = messageService.getPersonalizedMessageForKey("message.greeting." + token + "." + userName.toLowerCase(), "message.greeting." + token + ".default");
            messageTemplate = messageTemplate.replace(token, replacement);
        }
        return messageTemplate.replaceAll(" +", StringUtils.SPACE).trim();
    }

    @SafeVarargs
    private String buildEmotes(final String channelId, final int maxNumberOfEmotes, final List<String>... emoteSets) {
        final int setNumber = getRandomIntExponentially(emoteSets.length, 2);
        final int numberOfEmotes = getRandomIntExponentially(maxNumberOfEmotes, 2) + 1;

        final List<String> selectedSet = emoteSets[setNumber].parallelStream().filter(emote -> isEmote(channelId, emote)).collect(Collectors.toList());

        final StringBuilder emotePart = new StringBuilder();
        for (int i = 0; i < numberOfEmotes; i++) {
            final int index = getRandomIntExponentially(selectedSet.size(), 2);
            emotePart.append(StringUtils.SPACE).append(selectedSet.get(index));
        }
        return emotePart.toString().trim();
    }

    private int getRandomIntExponentially(final int bound, final int exponent) {
        final List<Integer> results = new ArrayList<>();
        for (int i = 0, j = bound; i < bound; i++, j--) {
            final int numberOfCopies = (int) Math.pow(j, exponent);
            results.addAll(Collections.nCopies(numberOfCopies, i));
        }
        return results.get(new Random().nextInt(results.size()));
    }

    private String sanitizeRequestMessage(final String message) {
        String sanitizedMessage = message;
        if (sanitizedMessage.contains(TAG_CHARACTER)) {
            final String taggedUserName = StringUtils.substringBefore(StringUtils.substringAfter(sanitizedMessage, TAG_CHARACTER), StringUtils.SPACE);
            sanitizedMessage = sanitizedMessage.replace(TAG_CHARACTER + taggedUserName, StringUtils.EMPTY);
        }
        if (sanitizedMessage.startsWith(",")) {
            sanitizedMessage = StringUtils.removeStart(sanitizedMessage, ",");
        }
        sanitizedMessage = sanitizedMessage.trim();
        return sanitizedMessage;
    }

    private int calculateResponseDelayTime(final DefaultMessageServiceImpl.MessageBuilder messageBuilder) {
        final int minDelayTime = 3;
        final int maxDelayTime = 15;
        final int[] dividerArray = IntStream.range(minDelayTime, maxDelayTime + 1).toArray();
        final int divider;
        final String message = messageBuilder.toString();
        if (message.length() / maxDelayTime == 0) {
            divider = minDelayTime;
        } else if (message.length() / maxDelayTime > dividerArray.length) {
            divider = maxDelayTime;
        } else {
            divider = dividerArray[(message.length() / maxDelayTime) > 0 ? (message.length() / maxDelayTime) - 1 : 0];
        }
        return message.length() * 1000 / divider;
    }

    private void greetWithDelay(final String channelName, final String userName, final DefaultMessageServiceImpl.MessageBuilder messageBuilder, final int delay, final ChannelMessageEvent event) {
        ChannelMessageEvent replyEvent = null;
        switch (getReplyType()) {
            case SEND_RESPONSE:
                final boolean startsWithTag = getRandomIntExponentially(2, 2) == 0;
                messageBuilder.withUserTag(TAG_CHARACTER + userName, startsWithTag);
                break;
            case SEND_REPLY:
                replyEvent = event;
                break;
            case SEND_MESSAGE:
                break;
        }
        messageService.sendMessageWithDelay(channelName, messageBuilder, delay, replyEvent);
        cacheService.cacheGreeting(channelName, userName);
    }

    private void sendMessageWithDelay(final String channelName, final String userName, final DefaultMessageServiceImpl.MessageBuilder messageBuilder, final int delay, final ChannelMessageEvent event) {
        switch (getReplyType()) {
            case SEND_RESPONSE:
                final boolean startsWithTag = getRandomIntExponentially(2, 2) == 0;
                final boolean isNoTagCharIncluded = getRandomIntExponentially(4, 2) == 1;
                final String tag = isNoTagCharIncluded ? userName : TAG_CHARACTER + userName;
                messageBuilder.withUserTag(tag, startsWithTag);
                messageService.sendMessageWithDelay(channelName, messageBuilder, delay, null);
                break;
            case SEND_REPLY:
                messageService.sendMessageWithDelay(channelName, messageBuilder, delay, event);
                break;
            case SEND_MESSAGE:
                messageService.sendMessageWithDelay(channelName, messageBuilder, delay, null);
                break;
        }
    }

    private MessageType getReplyType() {
        int random = new Random().nextInt(RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP.length);
        return RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP[random];
    }

    private enum MessageType {
        SEND_RESPONSE,
        SEND_REPLY,
        SEND_MESSAGE;
    }
}
