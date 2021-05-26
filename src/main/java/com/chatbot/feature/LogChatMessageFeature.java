package com.chatbot.feature;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.service.BotFeatureService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.util.FeatureEnum;

public class LogChatMessageFeature extends AbstractFeature {
    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();

    public LogChatMessageFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        if (!isFeatureActiveForChannel(FeatureEnum.LOGGING, event.getChannel().getName())) {
            return;
        }
        System.out.printf("Channel [%s] - User[%s] - Message [%s]%n",
                event.getChannel().getName(), event.getUser().getName(), event.getMessage()
        );
    }
}
