package com.chatbot.feature;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.ChannelService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultChannelServiceImpl;
import com.chatbot.util.FeatureEnum;

public abstract class AbstractFeature {
    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    protected final ChannelService channelService = DefaultChannelServiceImpl.getInstance();

    protected boolean isFeatureActiveForChannel(final FeatureEnum featureEnum, final String channelName) {
        return botFeatureService.isFeatureActive(featureEnum) && botFeatureService.isFeatureActiveForChannel(featureEnum, channelName);
    }

    protected boolean isUserIgnoredOnChannel(final String channelName, final String userName) {
        return channelService.isUserIgnored(channelName, userName);
    }
}
