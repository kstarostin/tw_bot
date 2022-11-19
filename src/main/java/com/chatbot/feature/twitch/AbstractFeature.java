package com.chatbot.feature.twitch;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.ChannelService;
import com.chatbot.service.MessageService;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultChannelServiceImpl;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.chatbot.util.FeatureEnum;

public abstract class AbstractFeature {
    protected static final String TAG_CHARACTER = "@";

    protected static final String COMMAND_SYNTAX = "!bot";

    protected final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    protected final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    protected final ChannelService channelService = DefaultChannelServiceImpl.getInstance();
    private final BotFeatureService featureService = DefaultBotFeatureServiceImpl.getInstance();

    protected final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    protected boolean isFeatureActive(final String channelName, final FeatureEnum featureEnum) {
        return featureService.isTwitchFeatureActive(channelName, featureEnum);
    }

    protected boolean isActiveOnLiveStreamOnly(final String channelName) {
        return configurationService.isActiveOnLiveStreamOnly(channelName);
    }

    protected boolean isStreamLive(final String channelName) {
        return channelService.isStreamLive(channelName);
    }

    protected boolean isCommand(final String message) {
        return message.toLowerCase().startsWith(COMMAND_SYNTAX);
    }
}
