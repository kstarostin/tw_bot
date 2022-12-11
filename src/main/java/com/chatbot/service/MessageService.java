package com.chatbot.service;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public interface MessageService {

    void sendMessage(String channelName, String responseMessage, ChannelMessageEvent event);
    void sendMessage(String channelName, String responseMessage, boolean isMuteChecked, ChannelMessageEvent event);
    void sendMessageWithDelay(String channelName, String responseMessage, int delay, ChannelMessageEvent event);
    String getStandardMessageForKey(String key);
}
