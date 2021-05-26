package com.chatbot.feature;

import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.chatbot.strategy.ChatResponseStrategy;
import com.chatbot.strategy.impl.DefaultChatResponseOnFollowStrategyImpl;

public class ChannelNotificationOnFollowFeature extends AbstractFeature {
    private final ChatResponseStrategy followResponseStrategy = DefaultChatResponseOnFollowStrategyImpl.getInstance();

    public ChannelNotificationOnFollowFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(FollowEvent.class, this::onFollow);
    }

    public void onFollow(final FollowEvent event) {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActiveForChannel(FeatureEnum.FOLLOW, channelName) || isUserIgnoredOnChannel(channelName, userName)) {
            return;
        }
        followResponseStrategy.respond(event);
    }
}
