package com.chatbot.service;

import com.github.twitch4j.chat.events.AbstractChannelEvent;

public interface MessageService {
    void respond(AbstractChannelEvent event, String responseMessage);

    void respondWithDelay(AbstractChannelEvent event, String responseMessage, int delay);
    String getStandardMessageForKey(String key);
}
