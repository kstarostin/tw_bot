package com.chatbot.feature;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.util.FeatureEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogChatMessageFeature extends AbstractFeature {
    private final Logger LOG = LoggerFactory.getLogger(LogChatMessageFeature.class);

    public LogChatMessageFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        LOG.debug("Channel [{}] - Event [{}] - User [{}] - Message [{}]", event.getChannel(), event.getClass().getName(), event.getUser(), event.getMessage());
        if (!isFeatureActiveForChannel(FeatureEnum.LOGGING, event.getChannel().getName())) {
            return;
        }
        LOG.info("Channel [{}] - User [{}] - Message [{}]", event.getChannel().getName(), event.getUser().getName(), event.getMessage());
    }
}
