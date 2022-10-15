package com.chatbot.feature;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.util.FeatureEnum;

public abstract class AbstractFeature {
    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();

    protected boolean isFeatureActive(final FeatureEnum featureEnum) {
        return botFeatureService.isFeatureActive(featureEnum);
    }
}
