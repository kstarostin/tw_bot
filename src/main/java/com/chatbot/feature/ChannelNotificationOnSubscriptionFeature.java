package com.chatbot.feature;

import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.chatbot.strategy.ChatResponseStrategy;
import com.chatbot.strategy.impl.DefaultChatResponseOnSubscriptionStrategyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelNotificationOnSubscriptionFeature extends AbstractFeature {
    private final Logger LOG = LoggerFactory.getLogger(ChannelNotificationOnSubscriptionFeature.class);

    private final ChatResponseStrategy subscriptionResponseStrategy = DefaultChatResponseOnSubscriptionStrategyImpl.getInstance();

    public ChannelNotificationOnSubscriptionFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(SubscriptionEvent.class, this::onSubscription);
    }

    public void onSubscription(final SubscriptionEvent event) {
        LOG.debug("Channel [{}] - Event [{}] - User [{}] - Plan [{}] - Months [{}] - GiftedBy [{}] - Message [{}]",
                event.getChannel(), event.getClass().getName(), event.getUser(), event.getSubscriptionPlan(), event.getMonths(), event.getGiftedBy(), event.getMessage());
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActiveForChannel(FeatureEnum.SUBSCRIPTION, channelName) || isUserIgnoredOnChannel(channelName, userName)) {
            return;
        }
        subscriptionResponseStrategy.respond(event);
    }
}
