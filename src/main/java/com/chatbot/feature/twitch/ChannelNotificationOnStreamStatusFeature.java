package com.chatbot.feature.twitch;

import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.eventsub.events.StreamOfflineEvent;
import com.github.twitch4j.eventsub.events.StreamOnlineEvent;

import static com.chatbot.util.emotes.TwitchEmote.Sets.POG;
import static com.chatbot.util.emotes.TwitchEmote.Sets.SAD;

public class ChannelNotificationOnStreamStatusFeature extends AbstractFeature {

    public ChannelNotificationOnStreamStatusFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(StreamOnlineEvent.class, this::onStreamOnline);
        eventHandler.onEvent(StreamOfflineEvent.class, this::onStreamOffline);
    }

    public void onStreamOnline(final StreamOnlineEvent event) {
        final String channelId = event.getBroadcasterUserId();
        final String channelName = event.getBroadcasterUserLogin();
        /*if (!isFeatureActive(channelName, FeatureEnum.STREAM)) {
            return;
        }*/
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder()
                .withEmotes(twitchEmoteService.buildRandomEmoteLine(channelId, 1, POG))
                .withText(messageService.getPersonalizedMessageForKey("message.stream.online." + channelName.toLowerCase(), "message.stream.online.default"));
        messageService.sendMessage(event.getBroadcasterUserLogin(), messageBuilder, null);
    }

    public void onStreamOffline(final StreamOfflineEvent event) {
        final String channelId = event.getBroadcasterUserId();
        final String channelName = event.getBroadcasterUserLogin();
        /*if (!isFeatureActive(channelName, FeatureEnum.STREAM)) {
            return;
        }*/
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder()
                .withEmotes(twitchEmoteService.buildRandomEmoteLine(channelId, 1, SAD))
                .withText(messageService.getPersonalizedMessageForKey("message.stream.offline." + channelName.toLowerCase(), "message.stream.offline.default"));
        messageService.sendMessage(event.getBroadcasterUserLogin(), messageBuilder, null);
    }
}
