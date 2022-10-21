package com.chatbot.service;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.TwitchHelix;

public interface TwitchClientService {
    ITwitchClient getTwitchClient();

    TwitchHelix getTwitchHelixClient();

    void buildClient();
}
