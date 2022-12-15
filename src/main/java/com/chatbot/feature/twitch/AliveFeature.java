package com.chatbot.feature.twitch;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.service.PeriodCacheService;
import com.chatbot.service.ModerationService;
import com.chatbot.service.TwitchEmoteService;
import com.chatbot.service.impl.DefaultPeriodCacheServiceImpl;
import com.chatbot.service.impl.DefaultModerationServiceImpl;
import com.chatbot.service.impl.DefaultTwitchEmoteService;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
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

import static com.chatbot.util.emotes.BotEmote.Sets.GREETING;
import static com.chatbot.util.emotes.BotEmote.Sets.HAPPY;
import static com.chatbot.util.emotes.BotEmote.Sets.POG;

public class AliveFeature extends AbstractFeature {
    private static final int RND_TRIGGER_MIN_PROBABILITY = 1;
    private static final int RND_TRIGGER_MAX_PROBABILITY = 100;

    private final static MessageType[] RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP = {
            MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE,
            MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY,
            MessageType.SEND_MESSAGE
    };

    private static final String USERNAME_TOKEN = "${name}";
    private static final String GREETING_TOKEN = "${greeting}";
    private static final String ADDITION_TOKEN = "${addition}";
    private static final Set<String> IGNORED_USERS_FOR_GREETING = Set.of("nightbot");

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
        if (isGreetingEnabled(channelName) && !isUserGreeted(channelName, userName) && !isUserIgnored(userName) && !isBotTagged(message)) {
            final String responseMessage = applyEmotes(channelId, buildGreetingText(userName), 3, GREETING, POG, HAPPY);
            greetWithDelay(channelName, userName, responseMessage, calculateResponseDelayTime(responseMessage), event);
        } else if (isBotTagged(message) || (isNoOneTagged(message) && isRandomTrigger(channelName))) {
            final String responseMessage = balabobaResponseGenerator.generate(sanitizeRequestMessage(message), true, true, false);
            if (StringUtils.isNotEmpty(responseMessage)) {
                sendMessageWithDelay(channelName, userName, responseMessage, calculateResponseDelayTime(responseMessage), event);
            }
        }
    }

    private boolean isBotTagged(final String message) {
        return StringUtils.containsIgnoreCase(message, configurationService.getBotName());
    }

    private boolean isUserGreeted(final String channelName, final String userName) {
        return cacheService.getCachedGreetings(channelName).isPresent() && cacheService.getCachedGreetings(channelName).get().contains(userName);
    }

    private boolean isUserIgnored(final String userName) {
        return IGNORED_USERS_FOR_GREETING.contains(userName.toLowerCase());
    }

    private boolean isGreetingEnabled(final String channelName) {
        return configurationService.getConfiguration(channelName).isUserGreetingEnabled();
    }

    private boolean isNoOneTagged(final String message) {
        return !message.contains(TAG_CHARACTER);
    }

    private boolean isRandomTrigger(final String channelName) {
        final SplittableRandom random = new SplittableRandom();
        return random.nextInt(RND_TRIGGER_MIN_PROBABILITY, RND_TRIGGER_MAX_PROBABILITY + 1) <= configurationService.getConfiguration(channelName).getIndependenceRate();
    }

    private String buildGreetingText(final String userName) {
        final List<String> greetingTokens = Arrays.asList(USERNAME_TOKEN, GREETING_TOKEN, ADDITION_TOKEN);
        Collections.shuffle(greetingTokens);
        final StringBuilder sb = new StringBuilder();
        final boolean startsWithTag = getRandomIntExponentially(2, 2) == 0;
        if (startsWithTag) {
            sb.append("%s").append(StringUtils.SPACE);
        }
        greetingTokens.forEach(token -> {
            final int exponent = ADDITION_TOKEN.equals(token) ? 3 : 2;
            if (getRandomIntExponentially(2, exponent) == 0) {
                sb.append(StringUtils.SPACE).append(token);
            }
        });
        if (!startsWithTag) {
            sb.append(StringUtils.SPACE).append("%s");
        }
        String messageTemplate = sb.toString();
        for (final String token : greetingTokens) {
            final String replacement = messageService.getPersonalizedMessageForKey("message.greeting." + token + "." + userName.toLowerCase(), "message.greeting." + token + ".default");
            messageTemplate = messageTemplate.replace(token, replacement);
        }
        return messageTemplate.replaceAll(" +", StringUtils.SPACE).trim();
    }

    @SafeVarargs
    private String applyEmotes(final String channelId, final String originalText, final int maxNumber, final List<String>... emoteSets) {
        final int setNumber = getRandomIntExponentially(emoteSets.length, 2);
        final int number = getRandomIntExponentially(maxNumber, 2) + 1;

        final List<String> selectedSet = emoteSets[setNumber].parallelStream().filter(emote -> twitchEmoteService.getValidEmoteNames(channelId).contains(emote)).collect(Collectors.toList());

        final StringBuilder emotePart = new StringBuilder();
        for (int i = 0; i < number; i++) {
            final int index = getRandomIntExponentially(selectedSet.size(), 2);
            emotePart.append(StringUtils.SPACE).append(selectedSet.get(index));
        }
        return StringUtils.isNotBlank(originalText) ? originalText + emotePart : emotePart.toString().trim();
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

    private int calculateResponseDelayTime(final String message) {
        final int minDelayTime = 3;
        final int maxDelayTime = 15;
        final int[] dividerArray = IntStream.range(minDelayTime, maxDelayTime + 1).toArray();
        final int divider;
        if (message.length() / maxDelayTime == 0) {
            divider = minDelayTime;
        } else if (message.length() / maxDelayTime > dividerArray.length) {
            divider = maxDelayTime;
        } else {
            divider = dividerArray[(message.length() / maxDelayTime) > 0 ? (message.length() / maxDelayTime) - 1 : 0];
        }
        return message.length() * 1000 / divider;
    }

    private void greetWithDelay(final String channelName, final String userName, final String message, final int delay, final ChannelMessageEvent event) {
        String responseMessage = message;
        ChannelMessageEvent replyEvent = null;
        switch (getReplyType()) {
            case SEND_RESPONSE:
                responseMessage = String.format(responseMessage, TAG_CHARACTER + userName);
                break;
            case SEND_REPLY:
                responseMessage = String.format(responseMessage, StringUtils.EMPTY).trim();
                replyEvent = event;
                break;
            case SEND_MESSAGE:
                responseMessage = String.format(responseMessage, StringUtils.EMPTY).trim();
                break;
        }
        messageService.sendMessageWithDelay(channelName, responseMessage, delay, replyEvent);
        cacheService.cacheGreeting(channelName, userName);
    }

    private void sendMessageWithDelay(final String channelName, final String userName, final String message, final int delay, final ChannelMessageEvent event) {
        switch (getReplyType()) {
            case SEND_RESPONSE:
                final boolean endsWithTag = new Random().nextInt(10) == 1; // whether tagging is placed in the beginning or in the end of the message; probability is 0.9 for the beginning, 0.1 for the end
                final boolean isNoTagCharIncluded = new Random().nextInt(20) == 1; // whether tagged name contains @; probability is 0.95 for included, 0.05 for not included
                final String taggedUserName = isNoTagCharIncluded ? userName : TAG_CHARACTER + userName;
                messageService.sendMessageWithDelay(channelName, endsWithTag ? (message + StringUtils.SPACE + taggedUserName) : (taggedUserName + StringUtils.SPACE + message), delay, null);
                break;
            case SEND_REPLY:
                messageService.sendMessageWithDelay(channelName, message, delay, event);
                break;
            case SEND_MESSAGE:
                messageService.sendMessageWithDelay(channelName, message, delay, null);
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
