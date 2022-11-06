package com.chatbot.feature.twitch;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.ChannelService;
import com.chatbot.service.MessageService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultChannelServiceImpl;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;
import com.chatbot.util.FeatureEnum;

public abstract class AbstractFeature {
    protected static final String TAG_CHARACTER = "@";

    private static final String COMMAND_SYNTAX = "!bot";

    protected final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    protected final StaticConfigurationService configurationService = DefaultStaticConfigurationServiceImpl.getInstance();
    protected final ChannelService channelService = DefaultChannelServiceImpl.getInstance();

    protected final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    protected boolean isFeatureActive(final FeatureEnum featureEnum) {
        return botFeatureService.isTwitchFeatureActive(featureEnum);
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
