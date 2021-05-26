package com.chatbot.strategy.impl;

import com.chatbot.entity.command.BotCommandActionEntity;
import com.chatbot.strategy.CommandActionStrategy;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public abstract class AbstractCommandActionStrategy implements CommandActionStrategy {

    @Override
    public abstract void execute(ChannelMessageEvent event, BotCommandActionEntity botCommandActionEntity);
}
