package com.chatbot.service.impl;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.github.twitch4j.helix.TwitchHelix;

public class DefaultTwitchClientServiceImpl implements TwitchClientService {
    private static DefaultTwitchClientServiceImpl instance;

    private ITwitchClient twitchClient;

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();

    private DefaultTwitchClientServiceImpl () {
        buildClient();
    }

    public static synchronized DefaultTwitchClientServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultTwitchClientServiceImpl();
        }
        return instance;
    }

    @Override
    public ITwitchClient getTwitchClient() {
        return twitchClient;
    }

    @Override
    public TwitchHelix getTwitchHelixClient() {
        return twitchClient.getHelix();
    }

    @Override
    public void buildClient() {
        if (twitchClient == null) {
            buildClientInternal();
        }
    }

    private void buildClientInternal() {
        final String accessToken = configurationService.getCredentialProperties().getProperty("twitch.credentials.irc");
        final OAuth2Credential credential = new OAuth2Credential("twitch", accessToken);
        final TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        twitchClient = clientBuilder
                .withClientId(configurationService.getCredentialProperties().getProperty("twitch.client.id"))
                .withClientSecret(configurationService.getCredentialProperties().getProperty("twitch.client.secret"))
                .withEnableHelix(true)
                /*
                 * Chat Module
                 * Joins irc and triggers all chat based events (viewer join/leave/sub/bits/gifted subs/...)
                 */
                .withChatAccount(credential)
                .withEnableChat(true)
                .withEnableTMI(true)
                .build();
    }
}
