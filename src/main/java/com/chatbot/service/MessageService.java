package com.chatbot.service;

public interface MessageService {

    void sendMessage(String channelName, String message);
    void sendMessageWithDelay(String channelName, String responseMessage, int delay);
    String getStandardMessageForKey(String key);
}
