package com.chatbot.feature.discord;

import com.chatbot.service.LoggerService;
import com.chatbot.service.impl.DefaultLoggerServiceImpl;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

public class LogChatMessageFeature extends AbstractDiscordFeature<MessageCreateEvent> {
    private static LogChatMessageFeature instance;

    private final LoggerService loggerService = DefaultLoggerServiceImpl.getInstance();

    private LogChatMessageFeature() {
    }

    public static synchronized LogChatMessageFeature getInstance() {
        if (instance == null) {
            instance = new LogChatMessageFeature();
        }
        return instance;
    }

    public Mono<Void> handle(final MessageCreateEvent event) {
        final Message message = event.getMessage();
        final String userName = message.getAuthor().map(User::getUsername).orElse(StringUtils.EMPTY);
        final String serverName = message.getGuild().map(Guild::getName).block();
        final String channelName = message.getGuild().map(guild -> guild.getChannelById(message.getChannelId()).block()).block().getName();

        loggerService.logDiscordMessage(serverName, channelName, userName, message.getContent());
        return Mono.empty();
    }
}
