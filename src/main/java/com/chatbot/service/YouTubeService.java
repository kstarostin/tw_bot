package com.chatbot.service;

import java.util.Optional;

public interface YouTubeService {

    String getRandomVideo(String channelId);
    Optional<String> getCachedRandomVideo(String channelId);
}
