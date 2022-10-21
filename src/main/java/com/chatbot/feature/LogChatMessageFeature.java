package com.chatbot.feature;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.util.FeatureEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class LogChatMessageFeature extends AbstractFeature {
    private final Logger LOG = LoggerFactory.getLogger(LogChatMessageFeature.class);

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public LogChatMessageFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        if (!isFeatureActive(FeatureEnum.LOGGING)) {
            return;
        }
        final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        LOG.info("Channel[{}]-[{}]:[{}]:[{}]", event.getChannel().getName(), formatter.format(event.getFiredAt().getTime()) , event.getUser().getName(), event.getMessage());
    }
}
