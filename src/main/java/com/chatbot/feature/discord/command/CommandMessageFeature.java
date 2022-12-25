package com.chatbot.feature.discord.command;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.apache.commons.lang3.StringUtils;

import reactor.core.publisher.Mono;

public class CommandMessageFeature extends AbstractCommandFeature<MessageCreateEvent> {
    private static CommandMessageFeature instance;

    private static final String COMMAND_SIGN = "!";

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
        final String responseMessage;

        if (StringUtils.startsWith(message.getContent(), COMMAND_SIGN + COMMAND_SUNBOY)) {
            responseMessage = handleSunboyCommand(message);
        } else if (StringUtils.startsWith(message.getContent(), COMMAND_SIGN + COMMAND_UFA)) {
            responseMessage = handleGenerateMessageForCommand(message, COMMAND_UFA, BalabobaResponseGenerator.Style.FOLK_WISDOM, BASEDGE_EMOTE);
        } else if (StringUtils.startsWith(message.getContent(), COMMAND_SIGN + COMMAND_STALKER)) {
            responseMessage = handleGenerateMessageForCommand(message, COMMAND_STALKER, BalabobaResponseGenerator.Style.SHORT_STORIES, STALK_2HEAD_EMOTE);
        } else {
            responseMessage = StringUtils.EMPTY;
        }

        return StringUtils.isNotEmpty(responseMessage) ? message.getChannel().flatMap(channel -> channel.createMessage(responseMessage)).then() : Mono.empty();
    }
}
