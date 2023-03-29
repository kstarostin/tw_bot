package com.chatbot.feature.discord;

import discord4j.core.event.domain.Event;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Set;

public abstract class AbstractDiscordFeature<T extends Event> implements DiscordFeature {

    public abstract Mono<Void> handle(final T event);
}
