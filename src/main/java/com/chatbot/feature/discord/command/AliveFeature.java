package com.chatbot.feature.discord.command;

import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.OpenAIResponseGenerator;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

public class AliveFeature {
    private static AliveFeature instance;

    private static final String COMMAND_SIGN = "!";

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final ResponseGenerator responseGenerator = OpenAIResponseGenerator.getInstance();

    private AliveFeature() {
    }

    public static synchronized AliveFeature getInstance() {
        if (instance == null) {
            instance = new AliveFeature();
        }
        return instance;
    }

    public Mono<Void> handle(final MessageCreateEvent event) {
        final Message message = event.getMessage();

        if (StringUtils.startsWith(message.getContent(), COMMAND_SIGN)) {
            return Mono.empty();
        }
        final boolean isBotMentioned = CollectionUtils.emptyIfNull(message.getUserMentions()).stream()
                .filter(User::isBot)
                .filter(user -> configurationService.getBotName().equalsIgnoreCase(user.getUsername()))
                .anyMatch(user -> "8705".equals(user.getDiscriminator()));
        if (!isBotMentioned) {
            return Mono.empty();
        }
        final String channelId = message.getChannelId().asString();
        final String userName = message.getAuthor().map(User::getUsername).orElse(StringUtils.EMPTY);
        final String userId = message.getAuthor().map(user -> user.getId().asString()).orElse(StringUtils.EMPTY);
        final String requesterId = "ds:" + channelId + ":" + userName;

        final String sanitizedContent = message.getContent().replaceAll("<@\\d+>", StringUtils.EMPTY).trim();

        final String responseMessage = responseGenerator.generate(requesterId, sanitizedContent, 250, true, false);

        return StringUtils.isNotEmpty(responseMessage)
                ? message.getChannel().flatMap(channel -> channel.createMessage(String.format("<@%s> %s", userId, responseMessage))).then()
                : Mono.empty();
    }
}
