package com.chatbot.service.impl;

import com.chatbot.service.ChannelService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.github.twitch4j.helix.domain.Stream;

import java.util.List;
import java.util.Optional;

public class DefaultChannelServiceImpl implements ChannelService {
    private static DefaultChannelServiceImpl instance;

    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();

    private final StaticConfigurationService staticConfigurationService = DefaultStaticConfigurationServiceImpl.getInstance();

    private DefaultChannelServiceImpl () {
    }

    public static synchronized DefaultChannelServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChannelServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean isStreamLive(final String channelName) {
        return getStream(channelName).isPresent();
    }

    @Override
    public Optional<Stream> getStream(final String channelName) {
        final String authToken = staticConfigurationService.getCredentialProperties().getProperty("twitch.credentials.access.token");
        final List<Stream> streams = twitchClientService.getTwitchHelixClient().getStreams(authToken, null, null, 1, null, null, null, List.of(channelName))
                .execute().getStreams();
        return streams.size() == 1 ? streams.stream().findFirst() : Optional.empty();
    }
}
