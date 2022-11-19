package com.chatbot.feature.twitch;

import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.eventsub.events.StreamOfflineEvent;
import com.github.twitch4j.eventsub.events.StreamOnlineEvent;

public class ChannelNotificationOnStreamStatusFeature extends AbstractFeature {

    public ChannelNotificationOnStreamStatusFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(StreamOnlineEvent.class, this::onStreamOnline);
        eventHandler.onEvent(StreamOfflineEvent.class, this::onStreamOffline);
    }

    public void onStreamOnline(final StreamOnlineEvent event) {
        final String channelName = event.getBroadcasterUserLogin();
        /*if (!isFeatureActive(channelName, FeatureEnum.STREAM)) {
            return;
        }*/
        final String notificationMessage = messageService.getStandardMessageForKey("message.stream.online");
        messageService.sendMessage(event.getBroadcasterUserLogin(), notificationMessage);
    }

    public void onStreamOffline(final StreamOfflineEvent event) {
        final String channelName = event.getBroadcasterUserLogin();
        /*if (!isFeatureActive(channelName, FeatureEnum.STREAM)) {
            return;
        }*/
        final String notificationMessage = messageService.getStandardMessageForKey("message.stream.offline");
        messageService.sendMessage(event.getBroadcasterUserLogin(), notificationMessage);
    }
}
