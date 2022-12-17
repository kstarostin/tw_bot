package com.chatbot.service;

import com.chatbot.service.impl.DefaultPeriodCacheServiceImpl;
import com.chatbot.service.impl.DefaultTwitchEmoteServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PeriodCacheService {
    void cacheGreeting(String channelName, String userName);
    Optional<Set<String>> getCachedGreetings(String channelName);
    void cacheEmotes(String channelName, DefaultTwitchEmoteServiceImpl.EmoteProvider provider, List emoteList, DefaultPeriodCacheServiceImpl.CachePeriod period);
    Optional<List> getCachedEmotes(String channelName, DefaultTwitchEmoteServiceImpl.EmoteProvider provider, DefaultPeriodCacheServiceImpl.CachePeriod period);

    void cacheYtVideo(String videoURL);
    Optional<String> getCachedYtVideo();
}
