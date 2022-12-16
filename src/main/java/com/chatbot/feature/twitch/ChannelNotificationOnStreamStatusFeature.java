package com.chatbot.feature.twitch;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.eventsub.events.StreamOfflineEvent;
import com.github.twitch4j.eventsub.events.StreamOnlineEvent;
import org.apache.commons.lang3.StringUtils;

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
        String notificationMessage = messageService.getStandardMessageForKey("message.stream.online." + channelName.toLowerCase());
        if (StringUtils.isEmpty(notificationMessage)) {
            notificationMessage = messageService.getStandardMessageForKey("message.stream.online.default");
        }
        messageService.sendMessage(event.getBroadcasterUserLogin(), messageService.getMessageBuilder().withText(notificationMessage), null);
    }

    public void onStreamOffline(final StreamOfflineEvent event) {
        final String channelName = event.getBroadcasterUserLogin();
        /*if (!isFeatureActive(channelName, FeatureEnum.STREAM)) {
            return;
        }*/
        String notificationMessage = messageService.getStandardMessageForKey("message.stream.offline." + channelName.toLowerCase());
        if (StringUtils.isEmpty(notificationMessage)) {
            notificationMessage = messageService.getStandardMessageForKey("message.stream.offline.default");
        }
        messageService.sendMessage(event.getBroadcasterUserLogin(), messageService.getMessageBuilder().withText(notificationMessage), null);
    }
}
