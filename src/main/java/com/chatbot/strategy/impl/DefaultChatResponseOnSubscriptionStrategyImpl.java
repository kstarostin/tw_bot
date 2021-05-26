package com.chatbot.strategy.impl;

import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.chatbot.strategy.ChatResponseStrategy;
import com.chatbot.util.EmoteEnum;

import static com.chatbot.util.MessageUtils.USER_TAG;

public class DefaultChatResponseOnSubscriptionStrategyImpl extends AbstractResponseStrategy implements ChatResponseStrategy {
    private static DefaultChatResponseOnSubscriptionStrategyImpl instance;

    private static final String MESSAGE_SUBSCRIPTION_DEFAULT = "message.subscription.default";

    private DefaultChatResponseOnSubscriptionStrategyImpl () {
    }

    public static synchronized DefaultChatResponseOnSubscriptionStrategyImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChatResponseOnSubscriptionStrategyImpl();
        }
        return instance;
    }

    @Override
    public void respond(final AbstractChannelEvent abstractEvent) {
        final SubscriptionEvent event = ((SubscriptionEvent) abstractEvent);
        final String responseMessage = String.format(messageService.getStandardMessageForKey(MESSAGE_SUBSCRIPTION_DEFAULT), USER_TAG + event.getUser().getName(), EmoteEnum.EZY);
        respond(event, responseMessage, event.getUser().getName());
    }
}
