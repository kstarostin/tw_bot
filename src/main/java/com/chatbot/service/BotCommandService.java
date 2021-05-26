package com.chatbot.service;

import com.chatbot.entity.command.BotCommandConfigurationEntity;
import com.chatbot.util.TechnicalBotCommandTriggerEnum;

import java.util.Optional;
import java.util.Set;

public interface BotCommandService {
    Set<BotCommandConfigurationEntity> getCommandsForChannel(String channelName);
    Set<String> getAllCommandTriggersForChannel(String channelName);
    Optional<BotCommandConfigurationEntity> getCommandForChannelAndMessage(String channelName, String message);
    Optional<TechnicalBotCommandTriggerEnum> extractTechnicalCommandTrigger(String channelName, String message);
}
