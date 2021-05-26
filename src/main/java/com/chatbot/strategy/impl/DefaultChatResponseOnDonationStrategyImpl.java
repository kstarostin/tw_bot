package com.chatbot.strategy.impl;

import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.DonationEvent;
import com.chatbot.strategy.ChatResponseStrategy;

public class DefaultChatResponseOnDonationStrategyImpl extends AbstractResponseStrategy implements ChatResponseStrategy {
    private static DefaultChatResponseOnDonationStrategyImpl instance;

    private static final String MESSAGE_DONATION_DEFAULT = "message.donation.default";

    private DefaultChatResponseOnDonationStrategyImpl () {
    }

    public static synchronized DefaultChatResponseOnDonationStrategyImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChatResponseOnDonationStrategyImpl();
        }
        return instance;
    }

    @Override
    public void respond(final AbstractChannelEvent abstractEvent) {
        final DonationEvent event = ((DonationEvent) abstractEvent);
        final String responseMessage = String.format(messageService.getStandardMessageForKey(MESSAGE_DONATION_DEFAULT), event.getUser().getName(), event.getAmount(), event.getSource());
        respond(event, responseMessage, event.getUser().getName());
    }
}
