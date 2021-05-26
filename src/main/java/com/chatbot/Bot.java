package com.chatbot;

import com.chatbot.dao.BotCommandActionTypeDAO;
import com.chatbot.dao.BotCommandTypeDAO;
import com.chatbot.dao.BotFeatureTypeDAO;
import com.chatbot.dao.ChannelConfigurationDAO;
import com.chatbot.dao.ChannelDAO;
import com.chatbot.dao.GlobalConfigurationDAO;
import com.chatbot.dao.impl.DefaultBotCommandActionTypeDAOImpl;
import com.chatbot.dao.impl.DefaultBotCommandTypeDAOImpl;
import com.chatbot.dao.impl.DefaultBotFeatureTypeDAOImpl;
import com.chatbot.dao.impl.DefaultChannelConfigurationDAOImpl;
import com.chatbot.dao.impl.DefaultChannelDAOImpl;
import com.chatbot.dao.impl.DefaultGlobalConfigurationDAOImpl;
import com.chatbot.entity.command.BotCommandActionTypeEntity;
import com.chatbot.entity.command.BotCommandConfigurationEntity;
import com.chatbot.entity.command.BotCommandSimpleResponseConfigurationEntity;
import com.chatbot.entity.command.BotCommandTriggerEntity;
import com.chatbot.entity.command.BotCommandTypeEntity;
import com.chatbot.entity.feature.BotFeatureTypeEntity;
import com.chatbot.entity.feature.ChannelBotFeatureCommandConfigurationEntity;
import com.chatbot.entity.feature.ChannelBotFeatureConfigurationEntity;
import com.chatbot.entity.feature.ChannelBotFeatureLoggingConfigurationEntity;
import com.chatbot.entity.config.ChannelConfigurationEntity;
import com.chatbot.entity.ChannelEntity;
import com.chatbot.entity.config.GlobalConfigurationEntity;
import com.chatbot.entity.command.BotCommandSimpleResponseActionEntity;
import com.chatbot.service.GlobalConfigurationService;
import com.chatbot.service.impl.DefaultGlobalConfigurationServiceImpl;
import com.chatbot.util.BotCommandActionEnum;
import com.chatbot.util.BotCommandEnum;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultTwitchClientServiceImpl;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.NoResultException;
import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Bot {
    private final StaticConfigurationService staticConfigurationService = DefaultStaticConfigurationServiceImpl.getInstance();
    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();

    private final GlobalConfigurationDAO globalConfigurationDAO = DefaultGlobalConfigurationDAOImpl.getInstance();
    private final ChannelDAO channelDAO = DefaultChannelDAOImpl.getInstance();
    private final BotFeatureTypeDAO botFeatureTypeDAO = DefaultBotFeatureTypeDAOImpl.getInstance();
    private final BotCommandTypeDAO botCommandTypeDAO = DefaultBotCommandTypeDAOImpl.getInstance();
    private final BotCommandActionTypeDAO botCommandActionTypeDAO = DefaultBotCommandActionTypeDAOImpl.getInstance();
    private final ChannelConfigurationDAO channelConfigurationDAO = DefaultChannelConfigurationDAOImpl.getInstance();

    private final GlobalConfigurationService globalConfigurationService = DefaultGlobalConfigurationServiceImpl.getInstance();

    public Bot() {
        staticConfigurationService.loadInitialStaticConfiguration();
        twitchClientService.buildClient();
        registerFeatures();
        //createSampleConfiguration();
        //removeChannelConfiguration();
        //removeChannelFromGlobalConfig();
    }

    public void start() {
        // Connect to all configured channels
        final Optional<GlobalConfigurationEntity> globalConfigurationEntityOptional = globalConfigurationService.getCurrentGlobalConfiguration();
        if (!globalConfigurationEntityOptional.isPresent()) {
            // todo log
            System.exit(1);
        }
        for (final ChannelEntity channelEntity : globalConfigurationEntityOptional.get().getConfiguredChannels()) {
            twitchClientService.getTwitchClient().getChat().joinChannel(channelEntity.getName());
        }
    }

    private void registerFeatures() {
        final SimpleEventHandler eventHandler = twitchClientService.getTwitchClient().getEventManager().getEventHandler(SimpleEventHandler.class);
        botFeatureService.registerAllFeatures(eventHandler);
    }

    private void createSampleConfiguration() {
        final String globalConfigurationCode = staticConfigurationService.getStaticConfiguration().getBot().get("name");

        ChannelEntity channel = getOrCreateSampleChannel();
        final Set<BotFeatureTypeEntity> allFeatureTypes = Arrays.stream(FeatureEnum.values())
                .map(this::getOrCreateBotFeatureType)
                .collect(Collectors.toSet());

        GlobalConfigurationEntity globalConfiguration;
        try {
            globalConfiguration = globalConfigurationDAO.getGlobalConfigurationByCode(globalConfigurationCode);
        } catch (final NoResultException e) {
            globalConfiguration = new GlobalConfigurationEntity();
            globalConfiguration.setCode(globalConfigurationCode);
            globalConfiguration.setSuperAdmin(channel);
            globalConfiguration = globalConfigurationDAO.create(globalConfiguration);
        }
        if (CollectionUtils.isEmpty(globalConfiguration.getActiveGlobalFeatureTypes())) {
            globalConfiguration.setActiveGlobalFeatureTypes(allFeatureTypes);
            globalConfiguration = globalConfigurationDAO.update(globalConfiguration);
        }
        if (channel.getGlobalConfiguration() == null) {
            channel.setGlobalConfiguration(globalConfiguration);
            channel = channelDAO.update(channel);
        }

        final Set<BotCommandTypeEntity> allCommandTypes = Arrays.stream(BotCommandEnum.values())
                .map(this::getOrCreateBotCommandType)
                .collect(Collectors.toSet());

        if (channel.getChannelConfiguration() == null) {
            final ChannelConfigurationEntity channelConfiguration = new ChannelConfigurationEntity();
            if (CollectionUtils.isEmpty(channelConfiguration.getAvailableChannelFeatureTypes())) {
                channelConfiguration.setAvailableChannelFeatureTypes(allFeatureTypes);
            }

            if (CollectionUtils.isEmpty(channelConfiguration.getConfiguredChannelFeatures())) {
                final Set<ChannelBotFeatureConfigurationEntity> configuredChannelFeatures = new HashSet<>();

                final ChannelBotFeatureLoggingConfigurationEntity loggingFeatureConfigEntity = new ChannelBotFeatureLoggingConfigurationEntity();
                loggingFeatureConfigEntity.setActive(true);
                loggingFeatureConfigEntity.setFeatureType(getOrCreateBotFeatureType(FeatureEnum.LOGGING));

                final ChannelBotFeatureCommandConfigurationEntity commandFeatureConfigEntity = new ChannelBotFeatureCommandConfigurationEntity();
                commandFeatureConfigEntity.setActive(true);
                commandFeatureConfigEntity.setFeatureType(getOrCreateBotFeatureType(FeatureEnum.COMMAND));

                configuredChannelFeatures.add(loggingFeatureConfigEntity);
                configuredChannelFeatures.add(commandFeatureConfigEntity);

                final BotCommandTriggerEntity botCommandTrigger = new BotCommandTriggerEntity();
                botCommandTrigger.setValue("test");

                final BotCommandSimpleResponseActionEntity simpleResponseBotCommandAction = new BotCommandSimpleResponseActionEntity();
                simpleResponseBotCommandAction.setResponseTemplate("test response");

                final BotCommandConfigurationEntity botCommandSimpleResponse = new BotCommandSimpleResponseConfigurationEntity();
                botCommandSimpleResponse.setCommandType(getOrCreateBotCommandType(BotCommandEnum.SIMPLE_RESPONSE));
                botCommandSimpleResponse.setCommandTriggers(Collections.singletonList(botCommandTrigger));
                botCommandSimpleResponse.setAvailableCommandActionTypes(Collections.singletonList(getOrCreateBotCommandActionType(BotCommandActionEnum.SIMPLE_RESPONSE)));
                botCommandSimpleResponse.setConfiguredCommandActions(Collections.singletonList(simpleResponseBotCommandAction));

                commandFeatureConfigEntity.setConfiguredChannelCommands(Collections.singleton(botCommandSimpleResponse));

                channelConfiguration.setConfiguredChannelFeatures(configuredChannelFeatures);
            }
            channel.addChannelConfiguration(channelConfiguration);
            channelDAO.update(channel);
        }
    }

    private void removeChannelConfiguration() {
        ChannelEntity channel = getOrCreateSampleChannel();
        channel.removeChannelConfiguration();
        channelDAO.update(channel);
    }

    private void removeChannelFromGlobalConfig() {
        ChannelEntity channel = getOrCreateSampleChannel();
        channel.removeGlobalConfiguration();
        channelDAO.update(channel);
    }

    private ChannelEntity getOrCreateSampleChannel() {
        final String channelName = "0mskBird";

        ChannelEntity channel;
        try {
            channel = channelDAO.getChannelByName(channelName);
        } catch (final NoResultException e) {
            channel = new ChannelEntity();
            channel.setName(channelName);
            channel = channelDAO.create(channel);
        }

        return channel;
    }

    private BotFeatureTypeEntity getOrCreateBotFeatureType(final FeatureEnum featureEnum) {
        BotFeatureTypeEntity botFeatureType;
        try {
            botFeatureType = botFeatureTypeDAO.getBotFeatureTypeByEnum(featureEnum);
        } catch (final NoResultException e) {
            botFeatureType = new BotFeatureTypeEntity();
            botFeatureType.setCode(featureEnum.toString());
            botFeatureType = botFeatureTypeDAO.create(botFeatureType);
        }
        return botFeatureType;
    }

    private BotCommandTypeEntity getOrCreateBotCommandType(final BotCommandEnum botCommandEnum) {
        BotCommandTypeEntity botCommandType;
        try {
            botCommandType = botCommandTypeDAO.getBotCommandTypeByEnum(botCommandEnum);
        } catch (final NoResultException e) {
            botCommandType = new BotCommandTypeEntity();
            botCommandType.setCode(botCommandEnum.toString());
            botCommandType = botCommandTypeDAO.create(botCommandType);
        }
        return botCommandType;
    }

    private BotCommandActionTypeEntity getOrCreateBotCommandActionType(final BotCommandActionEnum botCommandActionEnum) {
        BotCommandActionTypeEntity botCommandActionType;
        try {
            botCommandActionType = botCommandActionTypeDAO.getBotCommandActionTypeByEnum(botCommandActionEnum);
        } catch (final NoResultException e) {
            botCommandActionType = new BotCommandActionTypeEntity();
            botCommandActionType.setCode(botCommandActionEnum.toString());
            botCommandActionType = botCommandActionTypeDAO.create(botCommandActionType);
        }
        return botCommandActionType;
    }
}
