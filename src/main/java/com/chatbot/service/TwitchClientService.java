package com.chatbot.service;

import com.github.twitch4j.TwitchClient;

public interface TwitchClientService {
    TwitchClient getTwitchClient();
    void buildClient();
}
