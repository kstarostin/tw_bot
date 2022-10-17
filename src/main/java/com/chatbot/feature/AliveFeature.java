package com.chatbot.feature;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.service.MessageService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class AliveFeature extends AbstractFeature {

    private final static Set<String> GREETED_USERS = new HashSet<>();

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    private final StaticConfigurationService configurationService = DefaultStaticConfigurationServiceImpl.getInstance();
    private final ResponseGenerator balabobaResponseGenerator = BalabobaResponseGenerator.getInstance();

    public AliveFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String userName = event.getUser().getName();
        if (!isFeatureActive(FeatureEnum.ALIVE)) {
            return;
        }
        final String message = event.getMessage();
        if (isCommand(message)) {
            return;
        }
        if (isBotTagged(message)) {
            final String responseMessage = balabobaResponseGenerator.generate(message);
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.respondWithDelay(event, userName + " " + responseMessage, calculateResponseDelayTime(responseMessage));
            }
        } else if (!isUserGreeted(userName)) {
            final String responseMessage = String.format(messageService.getStandardMessageForKey("message.hello." + userName.toLowerCase()), userName);
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.respondWithDelay(event, responseMessage, calculateResponseDelayTime(responseMessage));
                GREETED_USERS.add(userName);
            }
        }
    }

    private boolean isBotTagged(final String message) {
        return StringUtils.containsIgnoreCase(message, configurationService.getBotName());
    }

    private boolean isUserGreeted(final String userName) {
        return GREETED_USERS.contains(userName);
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
            divider = dividerArray[message.length() / maxDelayTime];
        }
        return message.length() * 1000 / divider;
    }
}
