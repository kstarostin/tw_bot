package com.chatbot.service.impl;

import com.chatbot.feature.AliveFeature;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.chatbot.feature.*;
import com.chatbot.service.BotFeatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chatbot.util.FeatureEnum.*;

public class DefaultBotFeatureServiceImpl implements BotFeatureService {
    private static DefaultBotFeatureServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultBotFeatureServiceImpl.class);

    private final Map<FeatureEnum, Boolean> featureStateMap = Stream.of(new Object[][] {
            { ALIVE, Boolean.TRUE },
            { COMMAND, Boolean.TRUE },
            { LOGGING, Boolean.TRUE },
            { SUBSCRIPTION, Boolean.TRUE },
    }).collect(Collectors.toMap(data -> (FeatureEnum) data[0], data -> (Boolean) data[1]));

    private int randomAnswerProbability = 5;

    private static final String REGISTER_FEATURE = "Register feature: [{}]";

    private ChannelNotificationOnSubscriptionFeature channelNotificationOnSubscriptionFeature;
    private ChannelActionOnChatCommandFeature channelActionOnChatCommandFeature;
    private AliveFeature channelResponseOnChatEmoteSpammingFeature;
    private LogChatMessageFeature logChatMessageFeature;

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
        registerChannelNotificationOnSubscriptionFeature(eventHandler);
        registerChannelActionOnChatCommandFeature(eventHandler);
        registerChannelResponseOnChatEmoteSpammingFeature(eventHandler);
        registerLogChatMessageFeature(eventHandler);
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
        if (channelResponseOnChatEmoteSpammingFeature == null) {
            channelResponseOnChatEmoteSpammingFeature = new AliveFeature(eventHandler);
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
}
