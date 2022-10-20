package com.chatbot.service;

import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;

public interface BotFeatureService {
    void registerAllFeatures(SimpleEventHandler eventHandler);
    void registerChannelNotificationOnSubscriptionFeature(SimpleEventHandler eventHandler);
    void registerChannelActionOnChatCommandFeature(SimpleEventHandler eventHandler);
    void registerChannelResponseOnChatEmoteSpammingFeature(SimpleEventHandler eventHandler);
    void registerLogChatMessageFeature(SimpleEventHandler eventHandler);
    void registerChatModerationFeature(SimpleEventHandler eventHandler);

    boolean isFeatureActive(FeatureEnum featureEnum);
    void setFeatureStatus(FeatureEnum featureEnum, boolean isActive);

    int getRandomAnswerProbability();
    void setRandomAnswerProbability(int randomAnswerProbability);
}
