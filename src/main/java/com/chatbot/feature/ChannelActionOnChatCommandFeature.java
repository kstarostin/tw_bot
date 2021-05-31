package com.chatbot.feature;

import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.strategy.ChatResponseStrategy;
import com.chatbot.strategy.impl.DefaultChatActionOnCommandStrategyImpl;

public class ChannelActionOnChatCommandFeature extends AbstractFeature {
    private final ChatResponseStrategy commandResponseStrategy = DefaultChatActionOnCommandStrategyImpl.getInstance();

    public ChannelActionOnChatCommandFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActiveForChannel(FeatureEnum.COMMAND, channelName) && !isSuperAdmin(userName) || isUserIgnoredOnChannel(channelName, userName)) {
            return;
        }
        commandResponseStrategy.respond(event);
    }

    private boolean isSuperAdmin(final String channelName) {
        return channelService.isUserSuperAdmin(channelName);
    }
}
