package com.chatbot.service;

import java.util.Map;
import java.util.Optional;

public interface YouTubeService {

    String getRandomVideo(Map<String, Integer> channels);
    Optional<String> getCachedRandomVideo();
}
