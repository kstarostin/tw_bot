package com.chatbot.service.impl;

import com.chatbot.feature.twitch.AliveFeature;
import com.chatbot.feature.twitch.ChatCommandMessageFeature;
import com.chatbot.feature.twitch.ChannelNotificationOnStreamStatusFeature;
import com.chatbot.feature.twitch.ChannelNotificationOnSubscriptionFeature;
import com.chatbot.feature.twitch.ChatModerationFeature;
import com.chatbot.feature.twitch.LogChatMessageFeature;
import com.chatbot.service.ConfigurationService;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.chatbot.service.BotFeatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.chatbot.util.FeatureEnum.*;

public class DefaultBotFeatureServiceImpl implements BotFeatureService {
    private static DefaultBotFeatureServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultBotFeatureServiceImpl.class);

    private static final String REGISTER_FEATURE = "Register feature: [{}]";

    /**
     * Twitch features
     */
    private ChannelNotificationOnSubscriptionFeature channelNotificationOnSubscriptionFeature;
    //private ChannelNotificationOnStreamStatusFeature channelNotificationOnStreamStatusFeature;
    private ChatCommandMessageFeature chatCommandMessageFeature;
    private AliveFeature aliveFeature;
    private ChatModerationFeature chatModerationFeature;
    private LogChatMessageFeature logChatMessageFeature;

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();

    private DefaultBotFeatureServiceImpl () {
    }

    public static synchronized DefaultBotFeatureServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultBotFeatureServiceImpl();
        }
        return instance;
    }

    @Override
    public void registerAllTwitchFeatures(final SimpleEventHandler eventHandler) {
        registerLogChatMessageTwitchFeature(eventHandler);
        registerChannelNotificationOnSubscriptionTwitchFeature(eventHandler);
        registerChatCommandMessageTwitchFeature(eventHandler);
        registerAliveTwitchFeature(eventHandler);
        registerChatModerationTwitchFeature(eventHandler);
    }

    @Override
    public void registerChannelNotificationOnSubscriptionTwitchFeature(final SimpleEventHandler eventHandler) {
        if (channelNotificationOnSubscriptionFeature == null) {
            channelNotificationOnSubscriptionFeature = new ChannelNotificationOnSubscriptionFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, SUBSCRIPTION);
        }
    }

    @Override
    public void registerChatCommandMessageTwitchFeature(final SimpleEventHandler eventHandler) {
        if (chatCommandMessageFeature == null) {
            chatCommandMessageFeature = new ChatCommandMessageFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, COMMAND);
        }
    }

    @Override
    public void registerAliveTwitchFeature(final SimpleEventHandler eventHandler) {
        if (aliveFeature == null) {
            aliveFeature = new AliveFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, ALIVE);
        }
    }

    @Override
    public void registerLogChatMessageTwitchFeature(final SimpleEventHandler eventHandler) {
        if (logChatMessageFeature == null) {
            logChatMessageFeature = new LogChatMessageFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, LOGGING);
        }
    }

    @Override
    public void registerChatModerationTwitchFeature(final SimpleEventHandler eventHandler) {
        if (chatModerationFeature == null) {
            chatModerationFeature = new ChatModerationFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, MODERATOR);
        }
    }

    /*@Override
    public void registerChannelNotificationOnStreamStatusTwitchFeature(final SimpleEventHandler eventHandler) {
        if (channelNotificationOnStreamStatusFeature == null) {
            channelNotificationOnStreamStatusFeature = new ChannelNotificationOnStreamStatusFeature(eventHandler);
            LOG.info(REGISTER_FEATURE, STREAM);
        }
    }*/

    @Override
    public boolean isTwitchFeatureActive(final String channelName, final FeatureEnum featureEnum) {
        return configurationService.getConfiguration(channelName).getActiveFeatures().contains(featureEnum.name());
    }

    @Override
    public void setTwitchFeatureStatus(final String channelName, final FeatureEnum featureEnum, final boolean isActive) {
        if (isActive) {
            configurationService.getConfiguration(channelName).getActiveFeatures().add(featureEnum.name());
        } else {
            configurationService.getConfiguration(channelName).getActiveFeatures().remove(featureEnum.name());
        }
    }
}
