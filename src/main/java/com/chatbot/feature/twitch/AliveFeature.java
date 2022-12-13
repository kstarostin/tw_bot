package com.chatbot.feature.twitch;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.service.PeriodCacheService;
import com.chatbot.service.ModerationService;
import com.chatbot.service.impl.DefaultPeriodCacheServiceImpl;
import com.chatbot.service.impl.DefaultModerationServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;
import java.util.SplittableRandom;
import java.util.stream.IntStream;

public class AliveFeature extends AbstractFeature {
    private static final int RND_TRIGGER_MIN_PROBABILITY = 1;
    private static final int RND_TRIGGER_MAX_PROBABILITY = 100;

    private final static MessageType[] RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP = {
            MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE,
            MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY,
            MessageType.SEND_MESSAGE
    };

    private final ResponseGenerator balabobaResponseGenerator = BalabobaResponseGenerator.getInstance();
    private final ModerationService moderationService = DefaultModerationServiceImpl.getInstance();
    private final PeriodCacheService cacheService = DefaultPeriodCacheServiceImpl.getInstance();

    public AliveFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActive(channelName, FeatureEnum.ALIVE) || (isActiveOnLiveStreamOnly(channelName) && !isStreamLive(event.getChannel().getName()))) {
            return;
        }
        final String message = event.getMessage();
        if (isCommand(message) || moderationService.isSuspiciousMessage(channelName, message, event.getPermissions())) {
            return;
        }
        if (isGreetingEnabled(channelName) && !isUserGreeted(channelName, userName) && !isBotTagged(message)) {
            final String responseMessage = messageService.getPersonalizedMessageForKey("message.hello." + channelName.toLowerCase() + "." + userName.toLowerCase(), "message.hello.default." + userName.toLowerCase());
            if (StringUtils.isNotEmpty(responseMessage)) {
                greetWithDelay(channelName, userName, responseMessage, calculateResponseDelayTime(responseMessage), event);
            }
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
