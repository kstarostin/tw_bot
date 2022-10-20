package com.chatbot.feature;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.service.BotFeatureService;
import com.chatbot.service.MessageService;
import com.chatbot.service.ModerationService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultModerationServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.stream.IntStream;

public class AliveFeature extends AbstractFeature {

    private final static Set<String> GREETED_USERS = new HashSet<>();

    private static final String TAG_CHARACTER = "@";

    private static final int RND_TRIGGER_MIN_PROBABILITY = 1;
    private static final int RND_TRIGGER_MAX_PROBABILITY = 100;

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    private final ResponseGenerator balabobaResponseGenerator = BalabobaResponseGenerator.getInstance();
    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    private final ModerationService moderationService = DefaultModerationServiceImpl.getInstance();

    public AliveFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String userName = event.getUser().getName();
        if (!isFeatureActive(FeatureEnum.ALIVE) || (isActiveOnLiveStreamOnly() && !isStreamLive(event.getChannel().getName()))) {
            return;
        }
        final String message = event.getMessage();
        if (isCommand(message) || moderationService.isSuspiciousMessage(message, event.getPermissions())) {
            return;
        }
        if (!isUserGreeted(userName)) {
            final String responseMessage = String.format(messageService.getStandardMessageForKey("message.hello." + userName.toLowerCase()), TAG_CHARACTER + userName);
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.respondWithDelay(event, responseMessage, calculateResponseDelayTime(responseMessage));
                GREETED_USERS.add(userName);
            }
        } else if (isBotTagged(message) || (isNoOneTagged(message) && isRandomTrigger())) {
            final String responseMessage = balabobaResponseGenerator.generate(sanitizeMessage(message));
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.respondWithDelay(event, TAG_CHARACTER + userName + " " + responseMessage, calculateResponseDelayTime(responseMessage));
            }
        }
    }

    private boolean isBotTagged(final String message) {
        return StringUtils.containsIgnoreCase(message, configurationService.getBotName());
    }

    private boolean isUserGreeted(final String userName) {
        return GREETED_USERS.contains(userName);
    }

    private boolean isNoOneTagged(final String message) {
        return !message.contains(TAG_CHARACTER);
    }

    private boolean isRandomTrigger() {
        final SplittableRandom random = new SplittableRandom();
        return random.nextInt(RND_TRIGGER_MIN_PROBABILITY, RND_TRIGGER_MAX_PROBABILITY + 1) <= botFeatureService.getRandomAnswerProbability();
    }

    private String sanitizeMessage(String message) {
        final String taggedUserName = message.contains(TAG_CHARACTER)
                ? StringUtils.substringBefore(StringUtils.substringAfter(message, TAG_CHARACTER), StringUtils.SPACE)
                : StringUtils.EMPTY;
        return message.replace(TAG_CHARACTER + taggedUserName, StringUtils.EMPTY);
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
