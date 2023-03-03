package com.chatbot.feature.discord;

import com.chatbot.feature.generator.GeneratorRequest;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.impl.OpenAIResponseGenerator;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.DiscordEmoteService;
import com.chatbot.service.MessageService;
import com.chatbot.service.TrustManagerService;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultDiscordEmoteServiceImpl;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultTrustManagerServiceImpl;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateFields;
import discord4j.core.spec.MessageCreateMono;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chatbot.util.emotes.DiscordEmote.Sets.CONFUSION;
import static com.chatbot.util.emotes.DiscordEmote.Sets.HAPPY;

public class AliveFeature extends AbstractDiscordFeature<MessageCreateEvent> {
    private static AliveFeature instance;

    private final Logger LOG = LoggerFactory.getLogger(AliveFeature.class);

    private static final int REQUEST_MESSAGE_MAX_LENGTH = 150;

    private static final Set<String> IMAGE_REQUEST_KEY_WORDS = Set.of("нарисуй", "покажи", "изобрази", "продемонстрируй", "draw", "show", "imagine");

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    private final DiscordEmoteService discordEmoteService = DefaultDiscordEmoteServiceImpl.getInstance();
    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final ResponseGenerator openAIresponseGenerator = OpenAIResponseGenerator.getInstance();
    private final ResponseGenerator balabobaResponseGenerator = BalabobaResponseGenerator.getInstance();
    private final TrustManagerService trustManagerService = DefaultTrustManagerServiceImpl.getInstance();

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
        final String discriminator = message.getAuthor().map(User::getDiscriminator).orElse(StringUtils.EMPTY);

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

        String sanitizedRequestMessage = messageService.getMessageSanitizer(getRequestMessage(message, lastMessagesLimit))
                .withNoTags()
                .withNoEmotes()
                .withMaxLength(150)
                .sanitizeForDiscord();

        final boolean isImageRequested = isImageRequested(sanitizedRequestMessage);
        sanitizedRequestMessage = isImageRequested
                ? messageService.getMessageSanitizer(sanitizedRequestMessage)
                .withWordsRemoved(IMAGE_REQUEST_KEY_WORDS)
                .sanitizeForDiscord()
                : messageService.getMessageSanitizer(sanitizedRequestMessage)
                .withDelimiter()
                .sanitizeForDiscord();

        if (StringUtils.isBlank(sanitizedRequestMessage)) {
            return Mono.empty();
        }
        final GeneratorRequest.Builder generatorRequestBuilder = GeneratorRequest.getBuilder()
                .withRequestMessage(sanitizedRequestMessage)
                .withChannelId(channelId)
                .withUserName(userName + "#" + discriminator)
                .withResponseSanitized()
                .withMaxResponseLength(250);

        if (isImageRequested) {
            generatorRequestBuilder.withImageResponse();
        }

        final String responseText = generate(generatorRequestBuilder.buildForDiscord());

        final Optional<MessageCreateMono> discordResponseMessageOptional = createDiscordResponseMessage(message.getChannel().block(), responseText, isImageRequested, isUserTaggedInResponse ? userId : StringUtils.EMPTY);

        return discordResponseMessageOptional.map(messageCreateMono -> message.getChannel().flatMap(channel -> messageCreateMono).then()).orElseGet(Mono::empty);
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
                    .filter(lastMessage -> lastMessage.getTimestamp().isAfter(Instant.now().minus(1, ChronoUnit.DAYS))) // not older than a day ago
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

    private boolean isImageRequested(final String requestMessage) {
        return StringUtils.startsWithAny(requestMessage, IMAGE_REQUEST_KEY_WORDS.toArray(new String[0]));
    }

    private String generate(final GeneratorRequest request) {
        String response = openAIresponseGenerator.generate(request);
        if (StringUtils.isBlank(response) && !request.isImageResponse()) {
            response = balabobaResponseGenerator.generate(request);
        }
        return response;
    }

    private Optional<MessageCreateFields.File> getFileFromUrl(final String urlString) {
        try {
            trustManagerService.trustAllCertificates();
            final URL url = new URL(urlString);
            return Optional.of(MessageCreateFields.File.of(FilenameUtils.getName(url.getPath()), url.openStream()));
        } catch (final Exception e) {
            LOG.error("Can't read from resource [{}], error: {}", urlString, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<MessageCreateMono> createDiscordResponseMessage(final MessageChannel channel, final String responseText, final boolean isImageRequested, final String userId) {
        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder();

        if (!isImageRequested) {
            responseMessageBuilder.withText(responseText).withEmotes(discordEmoteService.buildRandomEmoteList(null, 2, CONFUSION, HAPPY));
            if (StringUtils.isNotEmpty(userId)) {
                responseMessageBuilder.withUserTag(userId);
            }
            return Optional.of(channel.createMessage(responseMessageBuilder.buildForDiscord()));
        } else {
            final Optional<MessageCreateFields.File> fileOptional = getFileFromUrl(responseText);

            if (fileOptional.isEmpty()) {
                return Optional.empty();
            }
            if (StringUtils.isNotEmpty(userId)) {
                responseMessageBuilder.withUserTag(userId);
            }
            return Optional.of(channel.createMessage(responseMessageBuilder.buildForDiscord()).withFiles(fileOptional.get()));
        }
    }
}
