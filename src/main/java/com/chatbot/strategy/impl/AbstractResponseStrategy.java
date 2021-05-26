package com.chatbot.strategy.impl;

import com.chatbot.service.GlobalConfigurationService;
import com.chatbot.service.impl.DefaultGlobalConfigurationServiceImpl;
import com.chatbot.strategy.ChatResponseStrategy;
import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultMessageServiceImpl;

public abstract class AbstractResponseStrategy implements ChatResponseStrategy {
    protected final StaticConfigurationService staticConfigurationService = DefaultStaticConfigurationServiceImpl.getInstance();
    protected final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    protected final GlobalConfigurationService globalConfigurationService = DefaultGlobalConfigurationServiceImpl.getInstance();

    @Override
    public abstract void respond(final AbstractChannelEvent abstractEvent);

    protected void respond(final AbstractChannelEvent event, final String responseMessage, final String userName) {
        messageService.respond(event, responseMessage, userName);
    }
}
