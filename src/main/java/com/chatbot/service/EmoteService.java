package com.chatbot.service;

import java.util.Set;

public interface EmoteService {
    Set<String> getAllEmotesForChannel(String channelName);
    Set<String> getBTTVEmotesForChannel(String channelName);
    Set<String> getFFZEmotesForChannel(String channelName);
    Set<String> getChannelEmotes(String channelName);
}
