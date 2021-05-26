package com.chatbot.strategy.impl;

import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.chatbot.strategy.ChatResponseStrategy;

public class DefaultChatResponseOnFollowStrategyImpl extends AbstractResponseStrategy implements ChatResponseStrategy {
    private static DefaultChatResponseOnFollowStrategyImpl instance;

    private static final String MESSAGE_FOLLOW_DEFAULT = "message.follow.default";

    private DefaultChatResponseOnFollowStrategyImpl() {
    }

    public static synchronized DefaultChatResponseOnFollowStrategyImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChatResponseOnFollowStrategyImpl();
        }
        return instance;
    }

    @Override
    public void respond(final AbstractChannelEvent abstractEvent) {
        final FollowEvent event = ((FollowEvent) abstractEvent);
        final String responseMessage = String.format(messageService.getStandardMessageForKey(MESSAGE_FOLLOW_DEFAULT), event.getUser().getName(), event.getChannel().getName());
        respond(event, responseMessage, event.getUser().getName());
    }
}
