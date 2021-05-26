package com.chatbot.service.impl;

import com.chatbot.entity.command.BotCommandConfigurationEntity;
import com.chatbot.entity.command.BotCommandTriggerEntity;
import com.chatbot.entity.feature.ChannelBotFeatureCommandConfigurationEntity;
import com.chatbot.entity.ChannelEntity;
import com.chatbot.service.BotCommandService;
import com.chatbot.service.ChannelService;
import com.chatbot.util.TechnicalBotCommandTriggerEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chatbot.util.CommandUtils.COMMAND_SIGN;

public class DefaultBotCommandServiceImpl implements BotCommandService {
    private static DefaultBotCommandServiceImpl instance;

    private final ChannelService channelService = DefaultChannelServiceImpl.getInstance();

    private DefaultBotCommandServiceImpl() {
    }

    public static synchronized DefaultBotCommandServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultBotCommandServiceImpl();
        }
        return instance;
    }

    @Override
    public Set<BotCommandConfigurationEntity> getCommandsForChannel(final String channelName) {
        final Optional<ChannelEntity> channelEntityOptional = channelService.getChannelByName(channelName);
        if (!channelEntityOptional.isPresent() || channelEntityOptional.get().getChannelConfiguration() == null
                || CollectionUtils.isEmpty(channelEntityOptional.get().getChannelConfiguration().getAvailableChannelFeatureTypes())
                || CollectionUtils.isEmpty(channelEntityOptional.get().getChannelConfiguration().getConfiguredChannelFeatures())) {
            return Collections.emptySet();
        }
        return channelEntityOptional.get().getChannelConfiguration().getConfiguredChannelFeatures().stream()
                .filter(featureConfigurationEntity -> featureConfigurationEntity instanceof ChannelBotFeatureCommandConfigurationEntity)
                .map(featureConfigurationEntity -> (ChannelBotFeatureCommandConfigurationEntity) featureConfigurationEntity)
                .filter(commandFeatureConfigurationEntity -> CollectionUtils.isNotEmpty(commandFeatureConfigurationEntity.getConfiguredChannelCommands()))
                .flatMap(commandFeatureConfigurationEntity -> commandFeatureConfigurationEntity.getConfiguredChannelCommands().stream())
                //.filter(channelCommand -> CollectionUtils.isNotEmpty(channelCommand.getCommandTriggers()))
                //.flatMap(channelCommand -> channelCommand.getCommandTriggers().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<BotCommandConfigurationEntity> getCommandForChannelAndMessage(final String channelName, final String message) {
        final Set<BotCommandConfigurationEntity> channelCommands = getCommandsForChannel(channelName);
        final String extractedCommandTrigger = extractCommandTrigger(channelName, message);
        return channelCommands.stream()
                .filter(botCommandEntity -> botCommandEntity.isActive() && getCommandTriggersForCommandEntity(botCommandEntity).contains(extractedCommandTrigger))
                .findFirst();
    }

    @Override
    public Set<String> getAllCommandTriggersForChannel(final String channelName) {
        return getCommandsForChannel(channelName).stream()
                .flatMap(botCommandEntity -> botCommandEntity.getCommandTriggers().stream())
                .map(BotCommandTriggerEntity::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<TechnicalBotCommandTriggerEnum> extractTechnicalCommandTrigger(final String channelName, final String message) {
        final Set<String> technicalCommandTriggers = Arrays.stream(TechnicalBotCommandTriggerEnum.values())
                .map(TechnicalBotCommandTriggerEnum::toString)
                .collect(Collectors.toSet());
        final String technicalBotCommandTriggerValue = extractCommandTrigger(technicalCommandTriggers, message);
        return technicalBotCommandTriggerValue != null ? Optional.of(TechnicalBotCommandTriggerEnum.valueOf(technicalBotCommandTriggerValue)) : Optional.empty();
    }

    private Set<String> getCommandTriggersForCommandEntity(final BotCommandConfigurationEntity botCommandConfigurationEntity) {
        return botCommandConfigurationEntity.getCommandTriggers().stream().map(BotCommandTriggerEntity::getValue).collect(Collectors.toSet());
    }

    private String extractCommandTrigger(final String channelName, final String message) {
        return extractCommandTrigger(getAllCommandTriggersForChannel(channelName), message);
    }

    private String extractCommandTrigger(final Set<String> triggers, final String message) {
        return triggers.stream()
                .filter(commandTrigger -> message.equalsIgnoreCase(COMMAND_SIGN + commandTrigger) || message.toLowerCase().startsWith(COMMAND_SIGN + commandTrigger + " "))
                .findFirst()
                .orElse(null);
    }
}
