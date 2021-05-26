package com.chatbot.service.impl;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.TwitchClientService;

public class DefaultTwitchClientServiceImpl implements TwitchClientService {
    private static DefaultTwitchClientServiceImpl instance;

    private TwitchClient twitchClient;

    private final StaticConfigurationService staticConfigurationService = DefaultStaticConfigurationServiceImpl.getInstance();

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
    public TwitchClient getTwitchClient() {
        return twitchClient;
    }

    @Override
    public void buildClient() {
        if (twitchClient == null) {
            buildClientInternal();
        }
    }

    private void buildClientInternal() {
        OAuth2Credential credential =
                new OAuth2Credential("twitch", staticConfigurationService.getStaticConfiguration().getCredentials().get("irc"));
        final TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        twitchClient = clientBuilder
                .withClientId(staticConfigurationService.getStaticConfiguration().getApi().get("twitch_client_id"))
                .withClientSecret(staticConfigurationService.getStaticConfiguration().getApi().get("twitch_client_secret"))
                .withEnableHelix(true)
                /*
                 * Chat Module
                 * Joins irc and triggers all chat based events (viewer join/leave/sub/bits/gifted subs/...)
                 */
                .withChatAccount(credential)
                .withEnableChat(true)
                /*
                 * GraphQL has a limited support
                 * Don't expect a bunch of features enabling it
                 */
                .withEnableGraphQL(true)
                /*
                 * Kraken is going to be deprecated
                 * see : https://dev.twitch.tv/docs/v5/#which-api-version-can-you-use
                 * It is only here so you can call methods that are not (yet)
                 * implemented in Helix
                 */
                .withEnableKraken(true)
                /*
                 * Build the TwitchClient Instance
                 */
                .build();
    }
}
