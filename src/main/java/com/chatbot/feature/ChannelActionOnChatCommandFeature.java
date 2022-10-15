package com.chatbot.feature;

import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class ChannelActionOnChatCommandFeature extends AbstractFeature {
    private static final String SUPER_ADMIN_NAME = "0mskbird";

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    public ChannelActionOnChatCommandFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String userName = event.getUser().getName();
        if (!isSuperAdmin(userName)) {
            return;
        }
        //messageService.respond(event, "");
    }

    private boolean isSuperAdmin(final String channelName) {
        return SUPER_ADMIN_NAME.equalsIgnoreCase(channelName);
    }
}
