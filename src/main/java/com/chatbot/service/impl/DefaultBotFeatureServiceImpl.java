package com.chatbot.service.impl;

import com.chatbot.feature.AliveFeature;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.chatbot.feature.*;
import com.chatbot.service.BotFeatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chatbot.util.FeatureEnum.*;

public class DefaultBotFeatureServiceImpl implements BotFeatureService {
    private static DefaultBotFeatureServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultBotFeatureServiceImpl.class);

    private final Map<FeatureEnum, Boolean> featureStateMap;

    private int randomAnswerProbability;

    private static final String REGISTER_FEATURE = "Register feature: [{}]";

    private ChannelNotificationOnSubscriptionFeature channelNotificationOnSubscriptionFeature;
    private ChannelActionOnChatCommandFeature channelActionOnChatCommandFeature;
    private AliveFeature aliveFeature;
    private ChatModerationFeature chatModerationFeature;
    private LogChatMessageFeature logChatMessageFeature;

    private final StaticConfigurationService configurationService = DefaultStaticConfigurationServiceImpl.getInstance();

    private DefaultBotFeatureServiceImpl () {
        final Set<FeatureEnum> activeFeatures = getActiveFeatures();
        featureStateMap = Arrays.stream(FeatureEnum.values()).collect(Collectors.toMap(feature -> feature, activeFeatures::contains));
        randomAnswerProbability = configurationService.getStaticConfiguration().getRandomAliveTriggerProbability();
    }

    public static synchronized DefaultBotFeatureServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultBotFeatureServiceImpl();
        }
        return instance;
    }

    @Override
    public void registerAllFeatures(final SimpleEventHandler eventHandler) {
        registerChannelNotificationOnSubscriptionFeature(eventHandler);
        registerChannelActionOnChatCommandFeature(eventHandler);
        registerChannelResponseOnChatEmoteSpammingFeature(eventHandler);
        registerLogChatMessageFeature(eventHandler);
        registerChatModerationFeature(eventHandler);
    }

    @Override
    public void registerChannelNotificationOnSubscriptionFeature(final SimpleEventHandler eventHandler) {
        if (channelNotificationOnSubscriptionFeature == null) {
            channelNotificationOnSubscriptionFeature = new ChannelNotificationOnSubscriptionFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, SUBSCRIPTION);
        }
    }

    @Override
    public void registerChannelActionOnChatCommandFeature(final SimpleEventHandler eventHandler) {
        if (channelActionOnChatCommandFeature == null) {
            channelActionOnChatCommandFeature = new ChannelActionOnChatCommandFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, COMMAND);
        }
    }

    @Override
    public void registerChannelResponseOnChatEmoteSpammingFeature(final SimpleEventHandler eventHandler) {
        if (aliveFeature == null) {
            aliveFeature = new AliveFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, ALIVE);
        }
    }

    @Override
    public void registerLogChatMessageFeature(final SimpleEventHandler eventHandler) {
        if (logChatMessageFeature == null) {
            logChatMessageFeature = new LogChatMessageFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, LOGGING);
        }
    }

    @Override
    public void registerChatModerationFeature(final SimpleEventHandler eventHandler) {
        if (chatModerationFeature == null) {
            chatModerationFeature = new ChatModerationFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, MODERATOR);
        }
    }

    @Override
    public boolean isFeatureActive(final FeatureEnum featureEnum) {
        return featureStateMap.containsKey(featureEnum) && featureStateMap.get(featureEnum);
    }

    @Override
    public void setFeatureStatus(final FeatureEnum featureEnum, final boolean isActive) {
        featureStateMap.put(featureEnum, isActive);
    }

    @Override
    public int getRandomAnswerProbability() {
        return randomAnswerProbability;
    }

    @Override
    public void setRandomAnswerProbability(final int randomAnswerProbability) {
        this.randomAnswerProbability = randomAnswerProbability;
    }

    private Set<FeatureEnum> getActiveFeatures() {
        return configurationService.getStaticConfiguration().getActiveFeatures().stream()
                .map(FeatureEnum::valueOf)
                .collect(Collectors.toSet());
    }
}
