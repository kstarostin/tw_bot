package com.chatbot.strategy;

import com.chatbot.entity.command.BotCommandActionEntity;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public interface CommandActionStrategy {
    void execute(ChannelMessageEvent event, BotCommandActionEntity botCommandActionEntity);
}
