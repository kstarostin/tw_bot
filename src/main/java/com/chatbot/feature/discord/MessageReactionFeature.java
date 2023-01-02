package com.chatbot.feature.discord;

import com.chatbot.service.ConfigurationService;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.chatbot.util.emotes.DiscordEmote;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class MessageReactionFeature extends AbstractDiscordFeature<MessageCreateEvent> {
    private static MessageReactionFeature instance;

    private final Logger LOG = LoggerFactory.getLogger(MessageReactionFeature.class);

    private static final Set<String> NO_STREAM_TODAY_STRING_TOKENS = Set.of("сегодня без", "сегодня не", "не будет", "не сегодня", "завтра", "в понедельник", "во вторник", "в среду",
            "в четверг", "в пятницу", "в субботу", "в воскресенье", "в день после", "а вот");

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();

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
        final String channelId = message.getChannelId().asString();
        if (!getWhitelistedChannelsForReactions().contains(channelId)) {
            return Mono.empty();
        }
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        LOG.info("Discord[{}]-[{}]:[{}]:[{}]", channelId, formatter.format(new Date()), message.getAuthor().map(User::getUsername).orElse(StringUtils.EMPTY), message.getContent());
        ReactionEmoji reaction = null;
        if (isEveryone(message.getContent())) {
            if (isNoStreamToday(message.getContent())) {
                reaction = getReaction(DiscordEmote.RedRoomGuild.Kippah, false);
            } else {
                reaction = getReaction(DiscordEmote.RedRoomGuild.Pausey, false);
            }
            LOG.info("Discord[{}]-[{}]:[{}]:[Reaction:{}]", channelId, formatter.format(new Date()), configurationService.getDiscordBotName(), reaction.asEmojiData().name().orElse(StringUtils.EMPTY));
        }
        if (hasStreamLink(message.getContent())) {
            reaction = getReaction(DiscordEmote.RedRoomGuild.Pogey, false);
            LOG.info("Discord[{}]-[{}]:[{}]:[Reaction:{}]", channelId, formatter.format(new Date()), configurationService.getDiscordBotName(), reaction.asEmojiData().name().orElse(StringUtils.EMPTY));
        }
        return reaction != null ? message.addReaction(reaction) : Mono.empty();
    }

    private boolean isNoStreamToday(final String content) {
        return NO_STREAM_TODAY_STRING_TOKENS.stream().anyMatch(token -> content.toLowerCase().contains(token));
    }

    private ReactionEmoji getReaction(final DiscordEmote emote, final boolean isAnimated) {
        return ReactionEmoji.of(emote.getId(), emote.getCode(), isAnimated);
    }
}
