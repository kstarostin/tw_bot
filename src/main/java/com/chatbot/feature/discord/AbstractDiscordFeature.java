package com.chatbot.feature.discord;

import discord4j.core.event.domain.Event;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Set;

public abstract class AbstractDiscordFeature<T extends Event> {

    /**
     * Discord channel IDs
     */
    protected static final String OMSK_OMSK = "799649888102645810";
    protected static final String RED_ROOM_ANNOUNCE = "950482716078538823";
    protected static final String RED_ROOM_TEXT = "950376827468267530";
    protected static final String RED_ROOM_SUNBOY_MANIA = "1038449077131673661";

    public abstract Mono<Void> handle(final T event);

    protected boolean isEveryone(final String message) {
        return StringUtils.containsIgnoreCase(message, "@everyone") || StringUtils.containsIgnoreCase(message, "@here");
    }

    protected boolean hasStreamLink(final String message) {
        return StringUtils.containsIgnoreCase(message, "https://www.twitch.tv/");
    }

    protected Set<String> getWhitelistedChannelsForCommands() {
        return Set.of(OMSK_OMSK, RED_ROOM_ANNOUNCE, RED_ROOM_TEXT, RED_ROOM_SUNBOY_MANIA);
    }

    protected Set<String> getWhitelistedChannelsForReactions() {
        return Set.of(OMSK_OMSK, RED_ROOM_ANNOUNCE);
    }
}
