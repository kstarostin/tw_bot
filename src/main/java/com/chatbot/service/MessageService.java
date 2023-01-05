package com.chatbot.service;

import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public interface MessageService {

    void sendMessage(String channelName, DefaultMessageServiceImpl.MessageBuilder messageBuilder, ChannelMessageEvent event);
    void sendMessage(String channelName, DefaultMessageServiceImpl.MessageBuilder messageBuilder, boolean isMuteChecked, ChannelMessageEvent event);
    void sendMessageWithDelay(String channelName, DefaultMessageServiceImpl.MessageBuilder messageBuilder, int delay, ChannelMessageEvent event);
    String getStandardMessageForKey(String key);
    String getPersonalizedMessageForKey(String personalizedKey, final String defaultKey);
    DefaultMessageServiceImpl.MessageBuilder getMessageBuilder();
    DefaultMessageServiceImpl.MessageSanitizer getMessageSanitizer(String text);
}
