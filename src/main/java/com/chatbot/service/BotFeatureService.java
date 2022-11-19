package com.chatbot.service;

import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;

public interface BotFeatureService {
    void registerAllTwitchFeatures(SimpleEventHandler eventHandler);
    void registerChannelNotificationOnSubscriptionTwitchFeature(SimpleEventHandler eventHandler);
    void registerChatCommandMessageTwitchFeature(SimpleEventHandler eventHandler);
    void registerAliveTwitchFeature(SimpleEventHandler eventHandler);
    void registerLogChatMessageTwitchFeature(SimpleEventHandler eventHandler);
    void registerChatModerationTwitchFeature(SimpleEventHandler eventHandler);
    //void registerChannelNotificationOnStreamStatusTwitchFeature(SimpleEventHandler eventHandler);

    boolean isTwitchFeatureActive(String channelName, FeatureEnum featureEnum);
    void setTwitchFeatureStatus(String channelName, FeatureEnum featureEnum, boolean isActive);
}
