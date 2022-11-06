package com.chatbot.service;

import java.util.Optional;
import java.util.Set;

public interface DayCacheService {
    void cacheGreeting(String userName);
    Optional<Set> getCachedGreetings();

    void cacheYtVideo(String videoURL);
    Optional<String> getCachedYtVideo();
}
