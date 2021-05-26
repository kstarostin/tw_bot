package com.chatbot.service;

import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.util.TechnicalBotCommandTriggerEnum;
import com.chatbot.util.EmoteEnum;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Date;
import java.util.Map;

public interface MessageService {
    void respond(AbstractChannelEvent event, String responseMessage, String userName);
    boolean isBotQuoted(ChannelMessageEvent event);
    boolean containsTrackedRepeatedEmote(ChannelMessageEvent event);
    void trackUserMessageForChannel(String channelName, String message);
    CircularFifoQueue<Map<String, Date>> getTrackedUserMessagesForChannel(String channelName);
    EmoteEnum extractEmotePart(ChannelMessageEvent event);
    String getStandardMessageForCommand(TechnicalBotCommandTriggerEnum commandEnum);
    String getStandardMessageForKey(String key);
}
