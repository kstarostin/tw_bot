package com.chatbot.feature.twitch;

import com.chatbot.service.LoggerService;
import com.chatbot.service.impl.DefaultLoggerServiceImpl;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.util.FeatureEnum;

public class LogChatMessageFeature extends AbstractFeature {

    private final LoggerService loggerService = DefaultLoggerServiceImpl.getInstance();

    public LogChatMessageFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        if (!isFeatureActive(event.getChannel().getName(), FeatureEnum.LOGGING)) {
            return;
        }
        loggerService.logTwitchMessage(event.getChannel().getName(), event.getUser().getName(), event.getMessage());
    }
}
