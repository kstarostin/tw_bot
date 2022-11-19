package com.chatbot.service.impl;

import com.chatbot.service.ChannelService;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.github.twitch4j.helix.domain.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class DefaultChannelServiceImpl implements ChannelService {
    private static DefaultChannelServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultChannelServiceImpl.class);

    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();

    private DefaultChannelServiceImpl () {
    }

    public static synchronized DefaultChannelServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChannelServiceImpl();
        }
        return instance;
    }

    @Override
    public void joinChannel(final String twitchChannelName) {
        configurationService.loadConfiguration(twitchChannelName);
        twitchClientService.getTwitchClient().getChat().joinChannel(twitchChannelName);
        LOG.info(String.format("Join Twitch channel: [%s]", twitchChannelName));
    }

    @Override
    public void leaveChannel(final String twitchChannelName) {
        //configurationService.loadConfiguration(twitchChannelName);
        twitchClientService.getTwitchClient().getChat().leaveChannel(twitchChannelName);
        LOG.info(String.format("Leave Twitch channel: [%s]", twitchChannelName));
    }

    @Override
    public boolean isStreamLive(final String channelName) {
        return getStream(channelName).isPresent();
    }

    @Override
    public Optional<Stream> getStream(final String channelName) {
        final String authToken = configurationService.getCredentialProperties().getProperty("twitch.credentials.access.token");
        final List<Stream> streams = twitchClientService.getTwitchHelixClient().getStreams(authToken, null, null, 1, null, null, null, List.of(channelName))
                .execute().getStreams();
        return streams.size() == 1 ? streams.stream().findFirst() : Optional.empty();
    }
}
