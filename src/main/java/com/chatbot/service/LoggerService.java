package com.chatbot.service;

public interface LoggerService {
    void logTwitchMessage(String channelName, String userName, String message);
    void logDiscordMessage(String serverName, String channelName, String userName, String message);
    void logDiscordReaction(String serverName, String channelName, String userName, String reaction);
}
