package com.chatbot.feature;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.DonationEvent;
import com.chatbot.strategy.ChatResponseStrategy;
import com.chatbot.strategy.impl.DefaultChatResponseOnDonationStrategyImpl;
import com.chatbot.util.FeatureEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelNotificationOnDonationFeature extends AbstractFeature {
    private final Logger LOG = LoggerFactory.getLogger(ChannelNotificationOnDonationFeature.class);

    private final ChatResponseStrategy donationResponseStrategy = DefaultChatResponseOnDonationStrategyImpl.getInstance();

    public ChannelNotificationOnDonationFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(DonationEvent.class, this::onDonation);
    }

    public void onDonation(final DonationEvent event) {
        LOG.debug("Channel [{}] - Event [{}] - User [{}] - Source [{}] - Amount [{}] - Currency [{}] - Message [{}]",
                event.getChannel(), event.getClass().getName(), event.getUser(), event.getSource(), event.getAmount(), event.getCurrency(), event.getMessage());
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActiveForChannel(FeatureEnum.DONATION, channelName) || isUserIgnoredOnChannel(channelName, userName)) {
            return;
        }
        donationResponseStrategy.respond(event);
    }
}
