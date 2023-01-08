package com.chatbot.feature.discord;

import com.chatbot.feature.generator.GeneratorRequest;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.impl.OpenAIResponseGenerator;
import com.chatbot.feature.generator.impl.util.ResponseGeneratorUtil;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.DiscordEmoteService;
import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultDiscordEmoteServiceImpl;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import static com.chatbot.util.emotes.DiscordEmote.Sets.CONFUSION;
import static com.chatbot.util.emotes.DiscordEmote.Sets.HAPPY;

public class AliveFeature extends AbstractDiscordFeature<MessageCreateEvent> {
    private static AliveFeature instance;

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    private final DiscordEmoteService discordEmoteService = DefaultDiscordEmoteServiceImpl.getInstance();
    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final ResponseGenerator openAIresponseGenerator = OpenAIResponseGenerator.getInstance();
    private final ResponseGenerator balabobaResponseGenerator = BalabobaResponseGenerator.getInstance();

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

        if (!isBotTagged(message)) {
            return Mono.empty();
        }
        final String channelId = message.getChannelId().asString();
        final String userName = message.getAuthor().map(User::getUsername).orElse(StringUtils.EMPTY);
        final String userId = message.getAuthor().map(user -> user.getId().asString()).orElse(StringUtils.EMPTY);

        final String sanitizedMessage = messageService.getMessageSanitizer(message.getContent())
                .withNoTags()
                .withNoEmotes()
                .withMaxLength(150)
                .withDelimiter()
                .sanitizeForDiscord();

        final String responseMessage = generate(GeneratorRequest.getBuilder()
                .withRequestMessage(sanitizedMessage)
                .withChannelId(channelId)
                .withUserName(userName)
                .withResponseSanitized()
                .withMaxResponseLength(250)
                .buildForDiscord());

        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder()
                .withUserTag(userId)
                .withText(responseMessage)
                .withEmotes(discordEmoteService.buildRandomEmoteList(null, 2, CONFUSION, HAPPY));

        return StringUtils.isNotEmpty(responseMessage)
                ? message.getChannel().flatMap(channel -> channel.createMessage(responseMessageBuilder.buildForDiscord())).then()
                : Mono.empty();
    }

    private boolean isBotTagged(final Message message) {
        return CollectionUtils.emptyIfNull(message.getUserMentions()).stream()
                .filter(User::isBot)
                .filter(user -> configurationService.getDiscordBotName().equalsIgnoreCase(user.getUsername()))
                .anyMatch(user -> configurationService.getDiscordBotDiscriminator().equals(user.getDiscriminator()));
    }

    private String generate(final GeneratorRequest request) {
        String response = openAIresponseGenerator.generate(request);
        if (StringUtils.isBlank(response)) {
            response = balabobaResponseGenerator.generate(request);
        }
        return StringUtils.isNotEmpty(response) ? ResponseGeneratorUtil.moderate(response) : response;
    }
}
