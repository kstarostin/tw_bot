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

    private static final String COMMAND_SIGN = "/";

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

        if ((COMMAND_SIGN + COMMAND_SUNBOY).equals(command)) {
            return handleSunboyCommand(event, channelId, userName, command);
        }
        if ((COMMAND_SIGN + COMMAND_UFA).equals(command)) {
            return handleUfaCommand(event, channelId, userName, command);
        }
        return Mono.empty();
    }

    private Mono<Void> handleSunboyCommand(final ChatInputInteractionEvent event, final String channelId, final String userName, final String command) {
        final Optional<String> textOptional = getOptionalText(event);
        if (hasCachedVideo()) {
            return event.reply().withContent(textOptional.isEmpty() ? handleSunboyCommand(channelId, userName, command) : handleSunboyCommand(channelId, userName, command, textOptional.get()));
        } else {
            event.reply(messageService.getStandardMessageForKey("message.discord.sunboy.inprogress")).subscribe();

            final String replyText = textOptional.isEmpty() ? handleSunboyCommand(channelId, userName, command) : handleSunboyCommand(channelId, userName, command, textOptional.get());
            event.editReply(InteractionReplyEditSpec.builder().build().withContentOrNull(replyText)).subscribe();
            return Mono.empty();
        }
    }

    private Mono<Void> handleUfaCommand(final ChatInputInteractionEvent event, final String channelId, final String userName, final String command) {
        final Optional<String> textOptional = getOptionalText(event);

        event.reply(messageService.getStandardMessageForKey("message.discord.ufa.inprogress")).subscribe();

        final String replyText = textOptional.isEmpty() ? handleUfaCommand(channelId, userName, command) : handleUfaCommand(channelId, userName, command, textOptional.get());
        event.editReply(InteractionReplyEditSpec.builder().build().withContentOrNull(replyText)).subscribe();
        return Mono.empty();
    }

    private Optional<String> getOptionalText(final ChatInputInteractionEvent event) {
        return event.getOption("text")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);
    }
}
