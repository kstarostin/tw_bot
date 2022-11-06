package com.chatbot.service;

public interface MessageService {

    void sendMessage(String channelName, String responseMessage);
    void sendMessage(String channelName, String responseMessage, boolean isMuteChecked);
    void sendMessageWithDelay(String channelName, String responseMessage, int delay);
    String getStandardMessageForKey(String key);
}
