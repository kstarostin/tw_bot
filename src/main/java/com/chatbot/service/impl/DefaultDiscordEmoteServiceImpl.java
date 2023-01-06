package com.chatbot.service.impl;

import com.chatbot.service.DiscordEmoteService;
import com.chatbot.util.emotes.DiscordEmote;

import java.util.List;
import java.util.Map;

public class DefaultDiscordEmoteServiceImpl extends AbstractEmoteServiceImpl<DiscordEmote> implements DiscordEmoteService {
    private static DefaultDiscordEmoteServiceImpl instance;

    private DefaultDiscordEmoteServiceImpl() {
    }

    public static synchronized DefaultDiscordEmoteServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultDiscordEmoteServiceImpl();
        }
        return instance;
    }

    @Override
    protected boolean isEmote(final String channelId, final String text) {
        return true;
    }

    @Override
    protected Map<DiscordEmote, List<DiscordEmote>> getEmoteCombinations() {
        return DiscordEmote.Sets.EMOTE_COMBINATIONS;
    }
}
