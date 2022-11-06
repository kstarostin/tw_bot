package com.chatbot.feature.twitch;

import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;

public class ChannelNotificationOnSubscriptionFeature extends AbstractFeature {

    public ChannelNotificationOnSubscriptionFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(SubscriptionEvent.class, this::onSubscription);
    }

    public void onSubscription(final SubscriptionEvent event) {
        final String userName = event.getUser().getName();
        if (!isFeatureActive(FeatureEnum.SUBSCRIPTION) || (isActiveOnLiveStreamOnly() && !isStreamLive(event.getChannel().getName()))) {
            return;
        }
        messageService.sendMessage(event.getChannel().getName(), String.format(messageService.getStandardMessageForKey("message.subscription.default"), userName));
    }
}
