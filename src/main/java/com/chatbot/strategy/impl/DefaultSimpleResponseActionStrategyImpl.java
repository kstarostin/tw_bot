package com.chatbot.strategy.impl;

import com.chatbot.entity.command.BotCommandActionEntity;
import com.chatbot.entity.command.BotCommandSimpleResponseActionEntity;
import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.strategy.CommandActionStrategy;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class DefaultSimpleResponseActionStrategyImpl extends AbstractCommandActionStrategy implements CommandActionStrategy {
    private static DefaultSimpleResponseActionStrategyImpl instance;

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    private DefaultSimpleResponseActionStrategyImpl() {
    }

    public static synchronized DefaultSimpleResponseActionStrategyImpl getInstance() {
        if (instance == null) {
            instance = new DefaultSimpleResponseActionStrategyImpl();
        }
        return instance;
    }

    @Override
    public void execute(final ChannelMessageEvent event, final BotCommandActionEntity botCommandActionEntity) {
        final BotCommandSimpleResponseActionEntity simpleResponseActionEntity = (BotCommandSimpleResponseActionEntity) botCommandActionEntity;
        final String responseTemplate = simpleResponseActionEntity.getResponseTemplate();
        messageService.respond(event, responseTemplate, event.getUser().getName());
    }
}
