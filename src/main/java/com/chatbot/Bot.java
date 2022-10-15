package com.chatbot;

import com.chatbot.configuration.Configuration;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultTwitchClientServiceImpl;

public class Bot {
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
        twitchClientService.getTwitchClient().getChat().joinChannel(staticConfiguration.getChannels().get("channel"));
    }

    private void registerFeatures() {
        final SimpleEventHandler eventHandler = twitchClientService.getTwitchClient().getEventManager().getEventHandler(SimpleEventHandler.class);
        botFeatureService.registerAllFeatures(eventHandler);
    }
}
