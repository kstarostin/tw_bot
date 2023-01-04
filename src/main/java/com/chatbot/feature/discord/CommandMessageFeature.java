package com.chatbot.feature.discord;

import com.chatbot.feature.generator.GeneratorRequest;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.MessageService;
import com.chatbot.service.YouTubeService;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultYouTubeServiceImpl;
import com.chatbot.util.emotes.DiscordEmote;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.InteractionReplyEditSpec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandMessageFeature extends AbstractDiscordFeature<ChatInputInteractionEvent> {
    private static CommandMessageFeature instance;

    private final Logger LOG = LoggerFactory.getLogger(CommandMessageFeature.class);

    private static final String COMMAND_SIGN = "/";

    protected static final String COMMAND_SUNBOY = "sunboy";
    protected static final String COMMAND_UFA = "ufa";
    protected static final String COMMAND_STALKER = "stalker";

    private static final String YOUTUBE_CHANNEL_ID_1 = "UC2WNW0NZVyMeEPvtLmScgvQ"; // SUNBOYUNITED
    private static final String YOUTUBE_CHANNEL_ID_2 = "UCBF5sbrlpTYECHMSfPT8wKw"; // Архив гениальных видео

    private final YouTubeService youTubeService = DefaultYouTubeServiceImpl.getInstance();
    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    private final ResponseGenerator responseGenerator = BalabobaResponseGenerator.getInstance();

    private CommandMessageFeature() {
    }

    public static synchronized CommandMessageFeature getInstance() {
        if (instance == null) {
            instance = new CommandMessageFeature();
        }
        return instance;
    }

    @Override
    public Mono<Void> handle(final ChatInputInteractionEvent event) {
        final String channelId = event.getInteraction().getChannelId().asString();
        final String userId = event.getInteraction().getUser().getId().asString();
        final String userName = event.getInteraction().getUser().getUsername();
        final String command = event.getInteraction().getCommandInteraction()
                .map(ApplicationCommandInteraction::getName)
                .map(nameOptional -> "/" + nameOptional.orElse(StringUtils.EMPTY))
                .orElse(StringUtils.EMPTY);

        if ((COMMAND_SIGN + COMMAND_SUNBOY).equals(command)) {
            return handleSunboyCommand(event, channelId, userId, userName, command);
        }
        if ((COMMAND_SIGN + COMMAND_UFA).equals(command)) {
            return handleUfaCommand(event, channelId, userId, userName, command);
        }
        if ((COMMAND_SIGN + COMMAND_STALKER).equals(command)) {
            return handleStalkerCommand(event, channelId, userId, userName, command);
        }
        return Mono.empty();
    }

    private Mono<Void> handleSunboyCommand(final ChatInputInteractionEvent event, final String channelId, final String userId, final String userName, final String command) {
        final Optional<String> textOptional = getOptionalText(event);
        if (hasCachedVideo()) {
            return event.reply().withContent(textOptional.isEmpty() ? handleSunboyCommand(channelId, userId, userName, command) : handleSunboyCommand(channelId, userName, command, textOptional.get()));
        } else {
            final DefaultMessageServiceImpl.MessageBuilder tempReplyBuilder = messageService.getMessageBuilder()
                    .withText(messageService.getStandardMessageForKey("message.discord.sunboy.inprogress"))
                    .withEmotes(List.of(DiscordEmote.KebirowHomeGuild.borpaSpin));
            event.reply(tempReplyBuilder.buildForDiscord()).subscribe();

            final String replyText = textOptional.isEmpty() ? handleSunboyCommand(channelId, userId, userName, command) : handleSunboyCommand(channelId, userId, userName, command, textOptional.get());
            event.editReply(InteractionReplyEditSpec.builder().build().withContentOrNull(replyText)).subscribe();
            return Mono.empty();
        }
    }

    private String handleSunboyCommand(final String channelId, final String userId, final String userName, final String content) {
        return handleSunboyCommand(channelId, userId, userName, content, null);
    }

    private String handleSunboyCommand(final String channelId, final String userId, final String userName, final String content, final String customResponseText) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        LOG.info("Discord[{}]-[{}]:[{}]:[{}]", channelId, formatter.format(new Date()), userName, content);

        final String videoURL = youTubeService.getRandomVideo(Map.of(YOUTUBE_CHANNEL_ID_1, 150, YOUTUBE_CHANNEL_ID_2, 50));
        final String responseMessage = StringUtils.isNotBlank(customResponseText)
                ? customResponseText + StringUtils.SPACE + videoURL
                : String.format(messageService.getStandardMessageForKey("message.discord.sunboy.response"), videoURL);

        LOG.info("Discord[{}]-[{}]:[{}]:[{}]", channelId, formatter.format(new Date()), configurationService.getDiscordBotName(), responseMessage);
        return responseMessage;
    }

    private Mono<Void> handleUfaCommand(final ChatInputInteractionEvent event, final String channelId, final String userId, final String userName, final String command) {
        final Optional<String> textOptional = getOptionalText(event);

        final DefaultMessageServiceImpl.MessageBuilder tempReplyBuilder = messageService.getMessageBuilder()
                .withText(messageService.getStandardMessageForKey("message.discord.ufa.inprogress"))
                .withEmotes(List.of(DiscordEmote.KebirowHomeGuild.VodkaTime));

        event.reply(tempReplyBuilder.buildForDiscord()).subscribe();

        final String replyText = textOptional.isEmpty()
                ? handleGenerateMessageForCommand(channelId, userId, userName, command, COMMAND_UFA, BalabobaResponseGenerator.Style.FOLK_WISDOM, DiscordEmote.KebirowHomeGuild.Basedge)
                : handleGenerateMessageForCommand(channelId, userId, userName, command, COMMAND_UFA, BalabobaResponseGenerator.Style.FOLK_WISDOM, DiscordEmote.KebirowHomeGuild.Basedge, textOptional.get());
        if (StringUtils.isNotEmpty(replyText)) {
            event.editReply(InteractionReplyEditSpec.builder().build().withContentOrNull(replyText)).subscribe();
        } else {
            event.editReply(InteractionReplyEditSpec.builder().build().withContentOrNull(messageService.getMessageBuilder()
                    .withUserTag(userId)
                    .withText(messageService.getStandardMessageForKey("message.discord.ufa.fail"))
                    .withEmotes(List.of(DiscordEmote.KebirowHomeGuild.Sadge))
                    .buildForDiscord())).subscribe();
        }
        return Mono.empty();
    }

    private Mono<Void> handleStalkerCommand(final ChatInputInteractionEvent event, final String channelId, final String userId, final String userName, final String command) {
        final Optional<String> textOptional = getOptionalText(event);

        final DefaultMessageServiceImpl.MessageBuilder tempReplyBuilder = messageService.getMessageBuilder()
                .withText(messageService.getStandardMessageForKey("message.discord.stalker.inprogress"))
                .withEmotes(List.of(DiscordEmote.KebirowHomeGuild.stalkPog));

        event.reply(tempReplyBuilder.buildForDiscord()).subscribe();

        final String replyText = textOptional.isEmpty()
                ? handleGenerateMessageForCommand(channelId, userId, userName, command, COMMAND_STALKER, BalabobaResponseGenerator.Style.SHORT_STORIES, DiscordEmote.KebirowHomeGuild.stalk2Head)
                : handleGenerateMessageForCommand(channelId, userId, userName, command, COMMAND_STALKER, BalabobaResponseGenerator.Style.SHORT_STORIES, DiscordEmote.KebirowHomeGuild.stalk2Head, textOptional.get());
        if (StringUtils.isNotEmpty(replyText)) {
            event.editReply(InteractionReplyEditSpec.builder().build().withContentOrNull(replyText)).subscribe();
        } else {
            event.editReply(InteractionReplyEditSpec.builder().build().withContentOrNull(messageService.getMessageBuilder()
                    .withUserTag(userId)
                    .withText(messageService.getStandardMessageForKey("message.discord.stalker.fail"))
                    .withEmotes(List.of(DiscordEmote.KebirowHomeGuild.Sadge))
                    .buildForDiscord())).subscribe();
        }
        return Mono.empty();
    }

    private boolean hasCachedVideo() {
        return youTubeService.getCachedRandomVideo().isPresent();
    }

    private String handleGenerateMessageForCommand(final String channelId, final String userId, final String userName, final String content, final String commandName,
                                                   final BalabobaResponseGenerator.Style style, final DiscordEmote emote) {
        return handleGenerateMessageForCommand(channelId, userId, userName, content, commandName, style, emote, null);
    }

    private String handleGenerateMessageForCommand(final String channelId, final String userId, final String userName, final String content, final String commandName,
                                                   final BalabobaResponseGenerator.Style style, final DiscordEmote emote, final String customStartText) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        LOG.info("Discord[{}]-[{}]:[{}]:[{}]", channelId, formatter.format(new Date()), userName, content);

        final StringBuilder textBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(customStartText)) {
            textBuilder.append(customStartText).append(StringUtils.SPACE);
        }
        final String requesterId = "ds:" + channelId + ":" + userName;
        final String requestMessage = messageService.getStandardMessageForKey("message.discord." + commandName + ".request");
        final String generatedMessage = responseGenerator.generate(new GeneratorRequest(requestMessage, requesterId, true, null, true, style));
        if (StringUtils.isBlank(generatedMessage) || StringUtils.equalsIgnoreCase(generatedMessage, requestMessage + StringUtils.SPACE)) {
            return StringUtils.EMPTY;
        }
        textBuilder.append(generatedMessage);

        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder()
                .withUserTag(userId)
                .withText(textBuilder.toString())
                .withEmotes(List.of(emote));

        final String response = responseMessageBuilder.buildForDiscord();
        LOG.info("Discord[{}]-[{}]:[{}]:[{}]", channelId, formatter.format(new Date()), configurationService.getDiscordBotName(), response);
        return response;
    }

    private Optional<String> getOptionalText(final ChatInputInteractionEvent event) {
        return event.getOption("text")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);
    }
}
