package com.chatbot;

import com.chatbot.configuration.Configuration;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultTwitchClientServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot {
    private final Logger LOG = LoggerFactory.getLogger(Bot.class);

    private final StaticConfigurationService staticConfigurationService = DefaultStaticConfigurationServiceImpl.getInstance();
    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();

    public Bot() {
        staticConfigurationService.loadInitialStaticConfiguration();
        twitchClientService.buildClient();
        registerFeatures();
    }

    public void start() {
        // Connect to all configured channels
        final Configuration staticConfiguration = staticConfigurationService.getStaticConfiguration();
        final String channelName = staticConfiguration.getChannels().get("channel");
        twitchClientService.getTwitchClient().getChat().joinChannel(channelName);
        LOG.info(String.format("Join channel: [%s]", channelName));
    }

    private void registerFeatures() {
        final SimpleEventHandler eventHandler = twitchClientService.getTwitchClient().getEventManager().getEventHandler(SimpleEventHandler.class);
        botFeatureService.registerAllFeatures(eventHandler);
    }
}
