package com.chatbot.feature.discord.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.apache.commons.lang3.StringUtils;

import reactor.core.publisher.Mono;

public class CommandMessageFeature extends AbstractCommandFeature<MessageCreateEvent> {
    private static CommandMessageFeature instance;

    private CommandMessageFeature() {
    }

    public static synchronized CommandMessageFeature getInstance() {
        if (instance == null) {
            instance = new CommandMessageFeature();
        }
        return instance;
    }

    @Override
    public Mono<Void> handle(final MessageCreateEvent event) {
        final Message message = event.getMessage();

        final String responseMessage = StringUtils.startsWithAny(message.getContent(), "!sunboy") ? handleCommand(message) : StringUtils.EMPTY;

        return StringUtils.isNotEmpty(responseMessage) ? message.getChannel().flatMap(channel -> channel.createMessage(responseMessage)).then() : Mono.empty();
    }
}
