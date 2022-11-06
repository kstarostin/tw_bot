package com.chatbot.feature.discord.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.InteractionReplyEditSpec;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class SlashCommandMessageFeature extends AbstractCommandFeature<ChatInputInteractionEvent> {
    private static SlashCommandMessageFeature instance;

    private SlashCommandMessageFeature() {
    }

    public static synchronized SlashCommandMessageFeature getInstance() {
        if (instance == null) {
            instance = new SlashCommandMessageFeature();
        }
        return instance;
    }

    @Override
    public Mono<Void> handle(final ChatInputInteractionEvent event) {
        final String channelId = event.getInteraction().getChannelId().asString();
        final String userName = event.getInteraction().getUser().getUsername();
        final String command = event.getInteraction().getCommandInteraction()
                .map(ApplicationCommandInteraction::getName)
                .map(nameOptional -> "/" + nameOptional.orElse(StringUtils.EMPTY))
                .orElse(StringUtils.EMPTY);
        final Optional<String> textOptional = getOptionalText(event);

        if (hasCachedVideo()) {
            return event.reply().withContent(textOptional.isEmpty() ? handleCommand(channelId, userName, command) : handleCommand(channelId, userName, command, textOptional.get()));
        } else {
            event.reply(messageService.getStandardMessageForKey("message.discord.sunboy.inprogress")).subscribe();

            final String replyText = textOptional.isEmpty() ? handleCommand(channelId, userName, command) : handleCommand(channelId, userName, command, textOptional.get());
            event.editReply(InteractionReplyEditSpec.builder().build().withContentOrNull(replyText)).subscribe();
            return Mono.empty();
        }
    }

    private Optional<String> getOptionalText(final ChatInputInteractionEvent event) {
        return event.getOption("text")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);
    }
}
