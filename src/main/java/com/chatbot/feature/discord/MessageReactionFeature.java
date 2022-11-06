package com.chatbot.feature.discord;

import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;
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
import java.util.Map;

public class MessageReactionFeature extends AbstractDiscordFeature<MessageCreateEvent> {
    private static MessageReactionFeature instance;

    private final Logger LOG = LoggerFactory.getLogger(MessageReactionFeature.class);

    /**
     * Discord emotes
     */
    private static final String PAUSEY = "Pausey";
    private static final String POGEY = "Pogey";
    private static final Map<String, Map<String, Long>> CHANNEL_2_EMOTE_MAP = Map.of(
            OMSK_OMSK, Map.of(PAUSEY, 1035132999798358016L, POGEY, 1035133015040462869L),
            RED_ROOM_ANNOUNCE, Map.of(PAUSEY, 987043617216536596L, POGEY, 980131980039573585L)
    );

    private final StaticConfigurationService staticConfigurationService = DefaultStaticConfigurationServiceImpl.getInstance();

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
            reaction = getReaction(channelId, "Pausey", false);
            LOG.info("Discord[{}]-[{}]:[{}]:[Reaction:{}]", channelId, formatter.format(new Date()), staticConfigurationService.getBotName(), reaction.asEmojiData().name().orElse(StringUtils.EMPTY));
        }
        if (hasStreamLink(message.getContent())) {
            reaction = getReaction(channelId, "Pogey", false);
            LOG.info("Discord[{}]-[{}]:[{}]:[Reaction:{}]", channelId, formatter.format(new Date()), staticConfigurationService.getBotName(), reaction.asEmojiData().name().orElse(StringUtils.EMPTY));
        }
        return reaction != null ? message.addReaction(reaction) : Mono.empty();
    }

    private ReactionEmoji getReaction(final String channelId, final String reactionName, final boolean isAnimated) {
        final Long emoteId = CHANNEL_2_EMOTE_MAP.get(channelId).getOrDefault(reactionName, null);
        return ReactionEmoji.of(emoteId, reactionName, isAnimated);
    }
}
