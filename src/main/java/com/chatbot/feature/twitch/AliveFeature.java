package com.chatbot.feature.twitch;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.service.DayCacheService;
import com.chatbot.service.ModerationService;
import com.chatbot.service.impl.DefaultDayCacheServiceImpl;
import com.chatbot.service.impl.DefaultModerationServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.SplittableRandom;
import java.util.stream.IntStream;

public class AliveFeature extends AbstractFeature {

    private static final int RND_TRIGGER_MIN_PROBABILITY = 1;
    private static final int RND_TRIGGER_MAX_PROBABILITY = 100;
    private final ResponseGenerator balabobaResponseGenerator = BalabobaResponseGenerator.getInstance();
    private final ModerationService moderationService = DefaultModerationServiceImpl.getInstance();
    private final DayCacheService dayCacheService = DefaultDayCacheServiceImpl.getInstance();

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
        if (isGreetingEnabled(channelName) && !isUserGreeted(userName)) {
            final String responseMessage = String.format(messageService.getStandardMessageForKey("message.hello." + userName.toLowerCase()), TAG_CHARACTER + userName);
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.sendMessageWithDelay(channelName, responseMessage, calculateResponseDelayTime(responseMessage));
                dayCacheService.cacheGreeting(userName);
            }
        } else if (isBotTagged(message) || (isNoOneTagged(message) && isRandomTrigger(channelName))) {
            final String responseMessage = balabobaResponseGenerator.generateShortSanitized(sanitizeRequestMessage(message), false);
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.sendMessageWithDelay(channelName, TAG_CHARACTER + userName + " " + responseMessage, calculateResponseDelayTime(responseMessage));
            }
        }
    }

    private boolean isBotTagged(final String message) {
        return StringUtils.containsIgnoreCase(message, configurationService.getBotName());
    }

    private boolean isUserGreeted(final String userName) {
        return dayCacheService.getCachedGreetings().isPresent() && dayCacheService.getCachedGreetings().get().contains(userName);
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
}
