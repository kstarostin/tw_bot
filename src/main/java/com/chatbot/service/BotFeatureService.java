package com.chatbot.service;

import com.chatbot.entity.feature.BotFeatureTypeEntity;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.chatbot.util.FeatureEnum;

import java.util.Optional;
import java.util.Set;

public interface BotFeatureService {
    void registerAllFeatures(SimpleEventHandler eventHandler);
    void registerChannelNotificationOnDonationFeature(SimpleEventHandler eventHandler);
    void registerChannelNotificationOnFollowFeature(SimpleEventHandler eventHandler);
    void registerChannelNotificationOnSubscriptionFeature(SimpleEventHandler eventHandler);
    void registerChannelActionOnChatCommandFeature(SimpleEventHandler eventHandler);
    void registerChannelResponseOnChatEmoteSpammingFeature(SimpleEventHandler eventHandler);
    void registerLogChatMessageFeature(SimpleEventHandler eventHandler);

    BotFeatureTypeEntity createBotFeatureType(BotFeatureTypeEntity entity);
    Optional<BotFeatureTypeEntity> getBotFeatureType(long entityId);
    Optional<BotFeatureTypeEntity> getBotFeatureTypeByCode(String featureCode);
    BotFeatureTypeEntity updateBotFeatureType(BotFeatureTypeEntity entity);
    void deleteBotFeatureType(BotFeatureTypeEntity entity);

    Set<FeatureEnum> getActiveGlobalFeatureTypes();
    Set<FeatureEnum> getActiveFeaturesForChannel(String channelName);
    void addFeatureToChannel(String channelName, FeatureEnum feature);
    void removeFeatureFromChannel(String channelName, FeatureEnum feature);
    boolean isFeatureActive(FeatureEnum feature);
    boolean isFeatureActiveForChannel(FeatureEnum feature, String channelName);
}
