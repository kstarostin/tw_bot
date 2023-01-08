package com.chatbot.service.impl;

import com.chatbot.service.DiscordEmoteService;
import com.chatbot.util.emotes.DiscordEmote;
import org.apache.commons.lang3.StringUtils;

public class DefaultDiscordEmoteServiceImpl extends AbstractEmoteServiceImpl<DiscordEmote> implements DiscordEmoteService {
    private static DefaultDiscordEmoteServiceImpl instance;

    private static final String DISCORD_EMOTE_FORMAT = "<.+:\\d{17,19}>";

    private DefaultDiscordEmoteServiceImpl() {
    }

    public static synchronized DefaultDiscordEmoteServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultDiscordEmoteServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean isEmote(final String text) {
        return isEmote(null, text);
    }

    @Override
    protected boolean isEmote(final String channelId, final String text) {
        return StringUtils.isNotEmpty(text) && text.matches(DISCORD_EMOTE_FORMAT);
    }
}
