package com.chatbot.feature.twitch;

import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;

public class ChannelNotificationOnSubscriptionFeature extends AbstractFeature {

    public ChannelNotificationOnSubscriptionFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(SubscriptionEvent.class, this::onSubscription);
    }

    public void onSubscription(final SubscriptionEvent event) {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActive(channelName, FeatureEnum.SUBSCRIPTION) || (isActiveOnLiveStreamOnly(channelName) && !isStreamLive(event.getChannel().getName()))) {
            return;
        }
        final String text = messageService.getPersonalizedMessageForKey("message.subscription." + channelName, "message.subscription.default");
        messageService.sendMessage(event.getChannel().getName(), messageService.getMessageBuilder().withUserTag(TAG_CHARACTER + userName).withText(text), null);
    }
}
