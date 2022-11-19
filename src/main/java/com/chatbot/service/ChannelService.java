package com.chatbot.service;

import com.github.twitch4j.helix.domain.Stream;

import java.util.Optional;

public interface ChannelService {
    void joinChannel(String twitchChannelName);
    void leaveChannel(String twitchChannelName);
    boolean isStreamLive(String channelName);
    Optional<Stream> getStream(String channelName);
}
