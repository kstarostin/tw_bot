package com.chatbot.service;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.TwitchHelix;

public interface TwitchClientService {
    TwitchClient getTwitchClient();

    TwitchHelix getTwitchHelixClient();

    void buildClient();
}
