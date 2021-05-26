package com.chatbot.feature;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.DonationEvent;
import com.chatbot.strategy.ChatResponseStrategy;
import com.chatbot.strategy.impl.DefaultChatResponseOnDonationStrategyImpl;
import com.chatbot.util.FeatureEnum;

public class ChannelNotificationOnDonationFeature extends AbstractFeature {
    private final ChatResponseStrategy donationResponseStrategy = DefaultChatResponseOnDonationStrategyImpl.getInstance();

    public ChannelNotificationOnDonationFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(DonationEvent.class, this::onDonation);
    }

    public void onDonation(final DonationEvent event) {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActiveForChannel(FeatureEnum.DONATION, channelName) || isUserIgnoredOnChannel(channelName, userName)) {
            return;
        }
        donationResponseStrategy.respond(event);
    }
}
