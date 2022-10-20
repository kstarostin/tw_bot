package com.chatbot.feature;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.ChannelService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultChannelServiceImpl;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;
import com.chatbot.util.FeatureEnum;

public abstract class AbstractFeature {
    protected final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    protected final StaticConfigurationService configurationService = DefaultStaticConfigurationServiceImpl.getInstance();
    protected final ChannelService channelService = DefaultChannelServiceImpl.getInstance();

    private static final String COMMAND_SYNTAX = "!bot";

    protected boolean isFeatureActive(final FeatureEnum featureEnum) {
        return botFeatureService.isFeatureActive(featureEnum);
    }

    protected boolean isActiveOnLiveStreamOnly() {
        return configurationService.isActiveOnLiveStreamOnly();
    }

    protected boolean isStreamLive(final String channelName) {
        return channelService.isStreamLive(channelName);
    }

    protected boolean isCommand(final String message) {
        return message.toLowerCase().startsWith(COMMAND_SYNTAX);
    }

    protected String getCommandSyntax() {
        return COMMAND_SYNTAX;
    }
}
