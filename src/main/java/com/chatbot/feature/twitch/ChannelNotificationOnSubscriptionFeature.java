package com.chatbot.feature.twitch;

import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;

import static com.chatbot.util.emotes.BotEmote.Sets.LAUGH;

public class ChannelNotificationOnSubscriptionFeature extends AbstractFeature {

    public ChannelNotificationOnSubscriptionFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(SubscriptionEvent.class, this::onSubscription);
    }

    public void onSubscription(final SubscriptionEvent event) {
        final String channelId = event.getChannel().getId();
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();

        if (!isFeatureActive(channelName, FeatureEnum.SUBSCRIPTION) || (isActiveOnLiveStreamOnly(channelName) && !isStreamLive(event.getChannel().getName()))) {
            return;
        }
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder()
                .withUserTag(TAG_CHARACTER + userName)
                .withText(messageService.getPersonalizedMessageForKey("message.subscription." + channelName, "message.subscription.default"))
                .withEmotes(twitchEmoteService.buildEmoteLine(channelId, 3, LAUGH));
        messageService.sendMessage(event.getChannel().getName(), messageBuilder, null);
    }
}
