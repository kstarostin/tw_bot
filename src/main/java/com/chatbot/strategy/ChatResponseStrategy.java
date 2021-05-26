package com.chatbot.strategy;

import com.github.twitch4j.chat.events.AbstractChannelEvent;

public interface ChatResponseStrategy {
    void respond(AbstractChannelEvent event);
}
