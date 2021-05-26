package com.chatbot.feature;

import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.strategy.ChatResponseStrategy;
import com.chatbot.strategy.impl.DefaultChatAliveResponseStrategyImpl;

public class AliveFeature extends AbstractFeature {
    private final ChatResponseStrategy aliveResponseStrategy = DefaultChatAliveResponseStrategyImpl.getInstance();

    public AliveFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActiveForChannel(FeatureEnum.ALIVE, channelName) || isUserIgnoredOnChannel(channelName, userName)) {
            return;
        }
        aliveResponseStrategy.respond(event);
    }
}
