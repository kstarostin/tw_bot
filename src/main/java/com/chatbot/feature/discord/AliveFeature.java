package com.chatbot.feature.discord;

import com.chatbot.feature.generator.GeneratorRequest;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.impl.OpenAIResponseGenerator;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.chatbot.util.emotes.DiscordEmote.Sets.CONFUSION;
import static com.chatbot.util.emotes.DiscordEmote.Sets.HAPPY;

public class AliveFeature extends AbstractDiscordFeature<MessageCreateEvent> {
    private static AliveFeature instance;

    private static final int REQUEST_MESSAGE_MAX_LENGTH = 150;

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
        final String userName = message.getAuthor().map(User::getUsername).orElse(StringUtils.EMPTY);

        if (isBotMessage(message)) {
            return Mono.empty();
        }
        final int lastMessagesLimit;
        final boolean isUserTaggedInResponse;
        if (isBotTaggedDirectly(message)) {
            lastMessagesLimit = 1;
            isUserTaggedInResponse = true;
        } else if (isBotTaggedIndirectly(message)) {
            lastMessagesLimit = 3;
            isUserTaggedInResponse = false;
        } else {
            return Mono.empty();
        }
        final String channelId = message.getChannelId().asString();
        final String userId = message.getAuthor().map(user -> user.getId().asString()).orElse(StringUtils.EMPTY);

        final String sanitizedRequestMessage = messageService.getMessageSanitizer(getRequestMessage(message, lastMessagesLimit))
                .withNoTags()
                .withNoEmotes()
                .withMaxLength(150)
                .withDelimiter()
                .sanitizeForDiscord();

        if (StringUtils.isBlank(sanitizedRequestMessage)) {
            return Mono.empty();
        }
        final String responseMessage = generate(GeneratorRequest.getBuilder()
                .withRequestMessage(sanitizedRequestMessage)
                .withChannelId(channelId)
                .withUserName(userName)
                .withResponseSanitized()
                .withMaxResponseLength(250)
                .buildForDiscord());

        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder()
                .withText(responseMessage)
                .withEmotes(discordEmoteService.buildRandomEmoteList(null, 2, CONFUSION, HAPPY));
        if (isUserTaggedInResponse) {
            responseMessageBuilder.withUserTag(userId);
        }
        return StringUtils.isNotEmpty(responseMessage)
                ? message.getChannel().flatMap(channel -> channel.createMessage(responseMessageBuilder.buildForDiscord())).then()
                : Mono.empty();
    }

    private boolean isBotMessage(final Message message) {
        return message.getAuthor().isPresent() && configurationService.getDiscordBotName().equalsIgnoreCase(message.getAuthor().get().getUsername());
    }

    private boolean isBotTaggedDirectly(final Message message) {
        return CollectionUtils.emptyIfNull(message.getUserMentions()).stream()
                .filter(User::isBot)
                .filter(user -> configurationService.getDiscordBotName().equalsIgnoreCase(user.getUsername()))
                .anyMatch(user -> configurationService.getDiscordBotDiscriminator().equals(user.getDiscriminator()));
    }

    private boolean isBotTaggedIndirectly(final Message message) {
        final List<String> additionalTags = new ArrayList<>(CollectionUtils.emptyIfNull(configurationService.getConfiguration("default").getAdditionalBotTagNames()));
        final String messageContent = message.getContent();
        for (final String tag : additionalTags) {
            if (messageContent.equalsIgnoreCase(tag)
                    || messageContent.toLowerCase().startsWith(tag.toLowerCase() + StringUtils.SPACE)
                    || messageContent.toLowerCase().startsWith(tag.toLowerCase() + ",")
                    || messageContent.toLowerCase().contains(StringUtils.SPACE + tag.toLowerCase() + StringUtils.SPACE)
                    || messageContent.toLowerCase().endsWith(StringUtils.SPACE + tag.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String getRequestMessage(final Message message, final int lastMessagesLimit) {
        final List<String> lastMessages = new ArrayList<>(List.of(message.getContent()));
        if (lastMessagesLimit > 1) {
            lastMessages.addAll(message.getChannel().block().getMessagesBefore(message.getId()).collectList().block().stream()
                    .filter(lastMessage -> !isBotMessage(lastMessage))
                    .limit(lastMessagesLimit - 1)
                    .map(Message::getContent)
                    .collect(Collectors.toList()));
        }
        final StringBuilder messageBuilder = new StringBuilder();
        final List<String> messagesToJoin = new ArrayList<>();
        for (final String messageToJoin : lastMessages) {
            messagesToJoin.add(messageToJoin);
            messageBuilder.append(messageToJoin).append(StringUtils.SPACE);
            if (messageBuilder.length() > REQUEST_MESSAGE_MAX_LENGTH) {
                break;
            }
        }
        return String.join(StringUtils.SPACE, messagesToJoin);
    }

    private String generate(final GeneratorRequest request) {
        String response = openAIresponseGenerator.generate(request);
        if (StringUtils.isBlank(response)) {
            response = balabobaResponseGenerator.generate(request);
        }
        return response;
    }
}
