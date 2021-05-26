package com.chatbot.service.impl;

import com.chatbot.dao.BotFeatureTypeDAO;
import com.chatbot.dao.impl.DefaultBotFeatureTypeDAOImpl;
import com.chatbot.entity.config.GlobalConfigurationEntity;
import com.chatbot.entity.feature.BotFeatureTypeEntity;
import com.chatbot.entity.feature.ChannelBotFeatureCommandConfigurationEntity;
import com.chatbot.entity.feature.ChannelBotFeatureConfigurationEntity;
import com.chatbot.entity.feature.ChannelBotFeatureLoggingConfigurationEntity;
import com.chatbot.entity.config.ChannelConfigurationEntity;
import com.chatbot.entity.ChannelEntity;
import com.chatbot.service.ChannelService;
import com.chatbot.service.GlobalConfigurationService;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.chatbot.util.FeatureEnum;
import com.chatbot.feature.*;
import com.chatbot.service.BotFeatureService;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.NoResultException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultBotFeatureServiceImpl implements BotFeatureService {
    private static DefaultBotFeatureServiceImpl instance;

    private static final String REGISTER_FEATURE = "Register feature: [%s] \n";

    private ChannelNotificationOnDonationFeature channelNotificationOnDonationFeature;
    private ChannelNotificationOnFollowFeature channelNotificationOnFollowFeature;
    private ChannelNotificationOnSubscriptionFeature channelNotificationOnSubscriptionFeature;
    private ChannelActionOnChatCommandFeature channelActionOnChatCommandFeature;
    private AliveFeature channelResponseOnChatEmoteSpammingFeature;
    private LogChatMessageFeature logChatMessageFeature;

    private final BotFeatureTypeDAO botFeatureTypeDAO = DefaultBotFeatureTypeDAOImpl.getInstance();
    private final GlobalConfigurationService globalConfigurationService = DefaultGlobalConfigurationServiceImpl.getInstance();
    private final ChannelService channelService = DefaultChannelServiceImpl.getInstance();

    private DefaultBotFeatureServiceImpl () {
    }

    public static synchronized DefaultBotFeatureServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultBotFeatureServiceImpl();
        }
        return instance;
    }

    @Override
    public void registerAllFeatures(final SimpleEventHandler eventHandler) {
        registerChannelNotificationOnDonationFeature(eventHandler);
        registerChannelNotificationOnFollowFeature(eventHandler);
        registerChannelNotificationOnSubscriptionFeature(eventHandler);
        registerChannelActionOnChatCommandFeature(eventHandler);
        registerChannelResponseOnChatEmoteSpammingFeature(eventHandler);
        registerLogChatMessageFeature(eventHandler);
    }

    @Override
    public void registerChannelNotificationOnDonationFeature(final SimpleEventHandler eventHandler) {
        if (!isFeatureActive(FeatureEnum.DONATION)) {
            return;
        }
        if (channelNotificationOnDonationFeature == null) {
            channelNotificationOnDonationFeature = new ChannelNotificationOnDonationFeature(eventHandler);
            System.out.printf(REGISTER_FEATURE, FeatureEnum.DONATION);
        }
    }

    public void registerChannelNotificationOnFollowFeature(final SimpleEventHandler eventHandler) {
        if (!isFeatureActive(FeatureEnum.FOLLOW)) {
            return;
        }
        if (channelNotificationOnFollowFeature == null) {
            channelNotificationOnFollowFeature = new ChannelNotificationOnFollowFeature(eventHandler);
            System.out.printf(REGISTER_FEATURE, FeatureEnum.FOLLOW);
        }
    }

    @Override
    public void registerChannelNotificationOnSubscriptionFeature(final SimpleEventHandler eventHandler) {
        if (!isFeatureActive(FeatureEnum.SUBSCRIPTION)) {
            return;
        }
        if (channelNotificationOnSubscriptionFeature == null) {
            channelNotificationOnSubscriptionFeature = new ChannelNotificationOnSubscriptionFeature(eventHandler);
            System.out.printf(REGISTER_FEATURE, FeatureEnum.SUBSCRIPTION);
        }
    }

    @Override
    public void registerChannelActionOnChatCommandFeature(final SimpleEventHandler eventHandler) {
        if (!isFeatureActive(FeatureEnum.COMMAND)) {
            return;
        }
        if (channelActionOnChatCommandFeature == null) {
            channelActionOnChatCommandFeature = new ChannelActionOnChatCommandFeature(eventHandler);
            System.out.printf(REGISTER_FEATURE, FeatureEnum.COMMAND);
        }
    }

    @Override
    public void registerChannelResponseOnChatEmoteSpammingFeature(final SimpleEventHandler eventHandler) {
        if (!isFeatureActive(FeatureEnum.ALIVE)) {
            return;
        }
        if (channelResponseOnChatEmoteSpammingFeature == null) {
            channelResponseOnChatEmoteSpammingFeature = new AliveFeature(eventHandler);
            System.out.printf(REGISTER_FEATURE, FeatureEnum.ALIVE);
        }
    }

    @Override
    public void registerLogChatMessageFeature(final SimpleEventHandler eventHandler) {
        if (!isFeatureActive(FeatureEnum.LOGGING)) {
            return;
        }
        if (logChatMessageFeature == null) {
            logChatMessageFeature = new LogChatMessageFeature(eventHandler);
            System.out.printf(REGISTER_FEATURE, FeatureEnum.LOGGING);
        }
    }

    @Override
    public BotFeatureTypeEntity createBotFeatureType(BotFeatureTypeEntity entity) {
        return botFeatureTypeDAO.create(entity);
    }

    @Override
    public Optional<BotFeatureTypeEntity> getBotFeatureType(long entityId) {
        return Optional.ofNullable(botFeatureTypeDAO.read(entityId));
    }

    @Override
    public Optional<BotFeatureTypeEntity> getBotFeatureTypeByCode(String featureCode) {
        try {
            return Optional.of(botFeatureTypeDAO.getBotFeatureTypeByCode(featureCode));
        } catch (final NoResultException nre) {
            // todo log
            return Optional.empty();
        }
    }

    @Override
    public BotFeatureTypeEntity updateBotFeatureType(BotFeatureTypeEntity entity) {
        return botFeatureTypeDAO.update(entity);
    }

    @Override
    public void deleteBotFeatureType(BotFeatureTypeEntity entity) {
        botFeatureTypeDAO.delete(entity);
    }

    @Override
    public Set<FeatureEnum> getActiveGlobalFeatureTypes() {
        final Optional<GlobalConfigurationEntity> globalConfigurationEntityOptional = globalConfigurationService.getCurrentGlobalConfiguration();
        if (!globalConfigurationEntityOptional.isPresent()
                || CollectionUtils.isEmpty(globalConfigurationEntityOptional.get().getActiveGlobalFeatureTypes())) {
            return Collections.emptySet();
        }
        final Set<String> globalActiveFeatureTypeCodes = globalConfigurationEntityOptional.get().getActiveGlobalFeatureTypes().stream()
                .map(featureTypeEntity -> featureTypeEntity.getCode().toLowerCase())
                .collect(Collectors.toSet());
        return Arrays.stream(FeatureEnum.values())
                .filter(featureEnum -> globalActiveFeatureTypeCodes.contains(featureEnum.toString().toLowerCase()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FeatureEnum> getActiveFeaturesForChannel(final String channelName) {
        final Optional<ChannelEntity> channelEntityOptional = channelService.getChannelByName(channelName);
        if (!channelEntityOptional.isPresent()
                || channelEntityOptional.get().getChannelConfiguration() == null
                || CollectionUtils.isEmpty(channelEntityOptional.get().getChannelConfiguration().getAvailableChannelFeatureTypes())) {
            return Collections.emptySet();
        }
        final Set<FeatureEnum> globalActiveFeatureTypes = getActiveGlobalFeatureTypes();
        final Set<BotFeatureTypeEntity> availableChannelFeatureTypes = channelEntityOptional.get().getChannelConfiguration().getAvailableChannelFeatureTypes();
        final Set<String> channelActiveFeatureCodes = channelEntityOptional.get().getChannelConfiguration().getConfiguredChannelFeatures().stream()
                .filter(featureConfigurationEntity -> availableChannelFeatureTypes.contains(featureConfigurationEntity.getFeatureType()))
                .map(featureConfigurationEntity -> featureConfigurationEntity.getFeatureType().getCode().toLowerCase())
                .collect(Collectors.toSet());
        return Arrays.stream(FeatureEnum.values())
                .filter(featureEnum -> globalActiveFeatureTypes.contains(featureEnum) && channelActiveFeatureCodes.contains(featureEnum.toString().toLowerCase()))
                .collect(Collectors.toSet());
    }

    @Override
    public void addFeatureToChannel(final String channelName, final FeatureEnum feature) {
        final Optional<ChannelEntity> channelEntityOptional = channelService.getChannelByName(channelName);
        final Optional<BotFeatureTypeEntity> botFeatureTypeEntityOptional = getBotFeatureTypeByCode(feature.toString().toLowerCase());
        if (!channelEntityOptional.isPresent() || !botFeatureTypeEntityOptional.isPresent()) {
            return;
        }
        final ChannelConfigurationEntity channelConfigurationEntity = channelEntityOptional.get().getChannelConfiguration();
        if (channelConfigurationEntity == null
                || CollectionUtils.isEmpty(channelConfigurationEntity.getAvailableChannelFeatureTypes())
                || !channelConfigurationEntity.getAvailableChannelFeatureTypes().contains(botFeatureTypeEntityOptional.get())) {
            // todo create configuration automatically?
            return;
        }
        final Set<ChannelBotFeatureConfigurationEntity> configuredChannelFeatures = CollectionUtils.isEmpty(channelConfigurationEntity.getConfiguredChannelFeatures())
                ? new HashSet<>()
                : channelConfigurationEntity.getConfiguredChannelFeatures();
        configuredChannelFeatures.add(createFeatureConfiguration(feature, botFeatureTypeEntityOptional.get()));
        channelConfigurationEntity.setConfiguredChannelFeatures(configuredChannelFeatures);

        channelService.updateChannel(channelEntityOptional.get());
    }

    @Override
    public void removeFeatureFromChannel(final String channelName, final FeatureEnum feature) {
        final Optional<ChannelEntity> channelEntityOptional = channelService.getChannelByName(channelName);
        final Optional<ChannelConfigurationEntity> channelConfigurationEntityOptional = channelEntityOptional.map(ChannelEntity::getChannelConfiguration);
        if (!channelEntityOptional.isPresent() || !channelConfigurationEntityOptional.isPresent()
                || CollectionUtils.isEmpty(channelConfigurationEntityOptional.get().getConfiguredChannelFeatures())) {
            return;
        }
        final Set<ChannelBotFeatureConfigurationEntity> configuredChannelFeatures = channelConfigurationEntityOptional.get().getConfiguredChannelFeatures();
        configuredChannelFeatures.stream()
                .filter(featureConfigurationEntity -> featureConfigurationEntity.getFeatureType().getCode().equalsIgnoreCase(feature.toString()))
                .findFirst()
                .ifPresent(configuredChannelFeatures::remove);
        channelService.updateChannel(channelEntityOptional.get());
    }

    @Override
    public boolean isFeatureActive(final FeatureEnum feature) {
        return getActiveGlobalFeatureTypes().contains(feature);
    }

    @Override
    public boolean isFeatureActiveForChannel(final FeatureEnum feature, final String channelName) {
        return getActiveFeaturesForChannel(channelName).contains(feature);
    }

    private ChannelBotFeatureConfigurationEntity createFeatureConfiguration(final FeatureEnum feature, final BotFeatureTypeEntity botFeatureTypeEntity) {
        switch (feature) {
            case LOGGING:
                return createLoggingFeatureConfiguration(botFeatureTypeEntity);
            case COMMAND:
                return createCommandFeatureConfiguration(botFeatureTypeEntity);
            default:
                return null;
        }
    }

    private ChannelBotFeatureLoggingConfigurationEntity createLoggingFeatureConfiguration(final BotFeatureTypeEntity botFeatureTypeEntity) {
        final ChannelBotFeatureLoggingConfigurationEntity loggingConfigurationEntity = new ChannelBotFeatureLoggingConfigurationEntity();
        loggingConfigurationEntity.setActive(true);
        loggingConfigurationEntity.setFeatureType(botFeatureTypeEntity);
        return loggingConfigurationEntity;
    }

    private ChannelBotFeatureCommandConfigurationEntity createCommandFeatureConfiguration(final BotFeatureTypeEntity botFeatureTypeEntity) {
        final ChannelBotFeatureCommandConfigurationEntity commandConfigurationEntity = new ChannelBotFeatureCommandConfigurationEntity();
        commandConfigurationEntity.setActive(true);
        commandConfigurationEntity.setFeatureType(botFeatureTypeEntity);
        return commandConfigurationEntity;
    }
}
