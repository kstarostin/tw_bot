package com.chatbot.feature.discord;

import com.chatbot.service.ConfigurationService;
import com.chatbot.service.DiscordEmoteService;
import com.chatbot.service.LoggerService;
import com.chatbot.service.MessageService;
import com.chatbot.service.RandomizerService;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultDiscordEmoteServiceImpl;
import com.chatbot.service.impl.DefaultLoggerServiceImpl;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultRandomizerServiceImpl;
import com.chatbot.util.emotes.AbstractEmote;
import com.chatbot.util.emotes.DiscordEmote;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chatbot.util.emotes.DiscordEmote.Sets.CONFUSION;
import static com.chatbot.util.emotes.DiscordEmote.Sets.POG;
import static com.chatbot.util.emotes.DiscordEmote.Sets.COOL;
import static com.chatbot.util.emotes.DiscordEmote.Sets.DANCE;
import static com.chatbot.util.emotes.DiscordEmote.Sets.LAUGH;

public class MessageReactionFeature extends AbstractDiscordFeature<MessageCreateEvent> {
    private static MessageReactionFeature instance;

    private static final Set<String> NO_STREAM_TODAY_STRING_TOKENS = Set.of("сегодня без", "сегодня не", "не будет", "не сегодня", "завтра", "в понедельник", "во вторник", "в среду",
            "в четверг", "в пятницу", "в субботу", "в воскресенье", "в день после", "а вот", "цок");

    private static final String TWITCH_URL_PATTERN = "^(https:|www\\.)/{0,2}w{0,3}\\.?twitch.tv/[a-zA-Z_\\d]+/?";

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final DiscordEmoteService discordEmoteService = DefaultDiscordEmoteServiceImpl.getInstance();
    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    private final RandomizerService randomizerService = DefaultRandomizerServiceImpl.getInstance();
    private final LoggerService loggerService = DefaultLoggerServiceImpl.getInstance();

    private MessageReactionFeature() {
    }

    public static synchronized MessageReactionFeature getInstance() {
        if (instance == null) {
            instance = new MessageReactionFeature();
        }
        return instance;
    }

    @Override
    public Mono<Void> handle(final MessageCreateEvent event) {
        final Message message = event.getMessage();
        final String messageContent = message.getContent();

        return isEveryone(messageContent) || hasStreamLink(messageContent) ? handleReaction(message) : Mono.empty();
    }

    private Mono<Void> handleReaction(final Message message) {
        final String serverName = message.getGuild().map(Guild::getName).block();
        final String channelName = message.getGuild().map(guild -> guild.getChannelById(message.getChannelId()).block()).block().getName();
        final String channelId = message.getChannelId().asString();
        if (!getWhitelistedChannelsForReactions().contains(channelId)) {
            return Mono.empty();
        }

        List<DiscordEmote> discordEmotes = new ArrayList<>();
        if (isEveryone(message.getContent())) {
            if (isNoStreamToday(message.getContent())) {
                discordEmotes = discordEmoteService.buildRandomEmoteList(null, 1, List.of(DiscordEmote.RedRoomGuild.adixWxman, DiscordEmote.KebirowHomeGuild.Kippah));
            } else if (hasAttachment(message)) {
                discordEmotes = discordEmoteService.buildRandomEmoteList(null, 1, LAUGH);
            } else {
                discordEmotes = discordEmoteService.buildRandomEmoteList(null, 1, CONFUSION);
            }
        }
        if (hasStreamLink(message.getContent())) {
            discordEmotes = discordEmoteService.buildRandomEmoteList(null, 1, POG, COOL, DANCE);
        }

        loggerService.logDiscordReaction(serverName, channelName, configurationService.getDiscordBotName(), discordEmotes.stream().map(AbstractEmote::getCode).collect(Collectors.joining(StringUtils.SPACE)));
        if (CollectionUtils.isEmpty(discordEmotes)) {
            return Mono.empty();
        } else if (discordEmotes.size() == 1) {
            addReaction(message, discordEmotes.iterator().next());
            return Mono.empty();
        } else {
            discordEmotes.forEach(discordEmote -> addReaction(message, discordEmote));
            return Mono.empty();
        }
    }

    private void addReaction(final Message message, final DiscordEmote discordEmote) {
        message.addReaction(convert(discordEmote)).subscribe();
        if (discordEmote.isCombination() && randomizerService.flipCoin()) {
            final int index = randomizerService.rollDice(discordEmote.getCombinedWith().size());
            final DiscordEmote combinationEmote = (DiscordEmote) discordEmote.getCombinedWith().get(index);
            message.addReaction(convert(combinationEmote)).subscribe();
        }
    }

    private boolean isNoStreamToday(final String content) {
        return NO_STREAM_TODAY_STRING_TOKENS.stream().anyMatch(token -> content.toLowerCase().contains(token));
    }

    private boolean hasAttachment(final Message message) {
        return CollectionUtils.isNotEmpty(message.getAttachments());
    }

    private ReactionEmoji convert(final DiscordEmote emote) {
        return ReactionEmoji.of(emote.getId(), emote.getCode(), emote.isAnimated());
    }

    private boolean isEveryone(final String message) {
        return StringUtils.containsIgnoreCase(message, "@everyone") || StringUtils.containsIgnoreCase(message, "@here");
    }

    private boolean hasStreamLink(final String message) {
        return Arrays.stream(messageService.getMessageSanitizer(message).sanitizeForDiscord().split(StringUtils.SPACE))
                .anyMatch(token -> token.matches(TWITCH_URL_PATTERN));
    }

    private Set<String> getWhitelistedChannelsForReactions() {
        return Set.of(KEBIROW_GENERAL, RED_ROOM_ANNOUNCE);
    }
}
