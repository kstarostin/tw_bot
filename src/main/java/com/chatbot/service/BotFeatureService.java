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
    void registerChannelNotificationOnStreamStatusTwitchFeature(SimpleEventHandler eventHandler);

    boolean isTwitchFeatureActive(FeatureEnum featureEnum);
    void setTwitchFeatureStatus(FeatureEnum featureEnum, boolean isActive);

    int getRandomAnswerProbability();
    void setRandomAnswerProbability(int randomAnswerProbability);

    boolean isBotMuted();
    void setMuted(boolean isMuted);
}
