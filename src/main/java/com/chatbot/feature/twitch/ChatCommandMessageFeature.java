package com.chatbot.feature.twitch;

import com.chatbot.service.MessageService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultTwitchClientServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.chatbot.util.emotes.bttv.BTTVEmote;
import com.chatbot.util.emotes.ffz.FFZEmoticon;
import com.chatbot.util.emotes.seventv.SevenTVEmote;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.Emote;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static com.chatbot.util.emotes.TwitchEmote.Sets.CONFUSION;
import static com.chatbot.util.emotes.TwitchEmote.Sets.HAPPY;
import static com.chatbot.util.emotes.TwitchEmote.Sets.SAD;

public class ChatCommandMessageFeature extends AbstractFeature {
    private static final String FEATURE_COMMAND_SET_ARG = "set";
    private static final String FEATURE_COMMAND_ALL_ARG = "all";
    private static final String FEATURE_COMMAND_ON_ARG = "on";
    private static final String FEATURE_COMMAND_OFF_ARG = "off";
    private static final String FEATURE_COMMAND_STATUS_ARG = "status";

    private static final String COMMAND_MUTE_ARG = "mute";
    private static final String COMMAND_UNMUTE_ARG = "unmute";

    private static final String COMMAND_ALL_ARG = "all";

    private static final String COMMAND_JOIN_ARG = "join";
    private static final String COMMAND_LEAVE_ARG = "leave";

    private static final String COMMAND_LOAD_ARG = "load";
    private static final String COMMAND_RELOAD_ARG = "reload";
    private static final String COMMAND_RESET_ARG = "reset";

    private static final String COMMAND_CHANNELS_ARG = "channels";

    private static final String COMMAND_OFF_ARG = "off";

    private static final String COMMAND_SEND_ARG = "send";

    private static final String COMMAND_7TV_ARG = "7tv";
    private static final String COMMAND_BTTV_ARG = "bttv";
    private static final String COMMAND_FFZ_ARG = "ffz";
    private static final String COMMAND_EMOTES_ARG = "emotes";
    private static final String COMMAND_GLOBAL_ARG = "global";
    private static final String COMMAND_CHANNEL_ARG = "channel";

    private static final String MESSAGE_COMMAND_DEFAULT = "message.command.default.";
    private static final String MESSAGE_COMMAND_UNAUTHORIZED = "message.command.unauthorized.";
    private static final String MESSAGE_COMMAND_ERROR = "message.command.error.";
    private static final String MESSAGE_COMMAND_MUTE = "message.command.mute.";
    private static final String MESSAGE_COMMAND_UNMUTE = "message.command.unmute.";
    private static final String MESSAGE_COMMAND_JOIN = "message.command.join.";
    private static final String MESSAGE_COMMAND_LEAVE = "message.command.leave.";
    private static final String MESSAGE_COMMAND_LOAD_CONFIGURATION = "message.command.load.configuration.";
    private static final String MESSAGE_COMMAND_CHANNELS = "message.command.channels.";
    private static final String MESSAGE_COMMAND_SHUTDOWN = "message.command.shutdown.";
    private static final String MESSAGE_COMMAND_SEND = "message.command.send.";
    private static final String MESSAGE_COMMAND_EMOTE_EMPTY = "message.command.emote.empty.";

    private static final String CHANNEL_DEFAULT = "default";

    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    public ChatCommandMessageFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        if (!isCommand(event.getMessage())) {
            return;
        }
        final String channelId = event.getChannel().getId();
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();

        if (!isSuperAdmin(userName) && !isBroadcaster(userName, event.getChannel().getName())) {
            final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder()
                    .withUserTag(userName)
                    .withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(), MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT));
            messageService.sendMessage(channelName, messageBuilder, false, null);
        }
        final String[] commandArgs = parseCommandArgs(event.getMessage());

        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = executeCommand(commandArgs, userName, channelId, channelName);
        if (messageBuilder.isNotEmpty()) {
            messageService.sendMessage(channelName, messageBuilder.withUserTag(userName), false, null);
        } else {
            messageBuilder.withUserTag(userName)
                    .withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, CONFUSION))
                    .withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_ERROR + channelName.toLowerCase(), MESSAGE_COMMAND_ERROR + CHANNEL_DEFAULT));
            messageService.sendMessage(channelName, messageBuilder, false, null);
        }
    }

    private boolean isBroadcaster(final String userName, final String channelName) {
        return userName.equalsIgnoreCase(channelName);
    }

    private String[] parseCommandArgs(final String message) {
        final String commandContent = message.replace(COMMAND_SYNTAX, StringUtils.EMPTY).trim();
        if (StringUtils.isNotEmpty(commandContent) && commandContent.matches(COMMAND_SEND_ARG + "\\s[a-zA-Z_\\d]+\\s.+")) {
            final String[] channelAndMessageToSend = commandContent.replaceFirst(COMMAND_SEND_ARG, StringUtils.EMPTY).trim().split(StringUtils.SPACE, 2);
            return Arrays.stream(ArrayUtils.addAll(new String[]{COMMAND_SEND_ARG}, channelAndMessageToSend))
                    .filter(StringUtils::isNotEmpty).toArray(String[]::new);
        }
        return Arrays.stream(commandContent.split(StringUtils.SPACE)).filter(StringUtils::isNotEmpty).toArray(String[]::new);
    }

    private DefaultMessageServiceImpl.MessageBuilder executeCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        if (args.length == 0) {
            return messageService.getMessageBuilder();
        }
        switch (args[0].toLowerCase()) {
            case FEATURE_COMMAND_SET_ARG:
                return executeSetFeatureCommand(ArrayUtils.removeElement(args, args[0]), channelId, channelName);
            case FEATURE_COMMAND_STATUS_ARG:
                return executeStatusFeatureCommand(ArrayUtils.removeElement(args, args[0]), channelName);
            case COMMAND_MUTE_ARG:
                return executeMuteCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_UNMUTE_ARG:
                return executeUnmuteCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_JOIN_ARG:
                return executeJoinCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_LEAVE_ARG:
                return executeLeaveCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_LOAD_ARG:
            case COMMAND_RELOAD_ARG:
            case COMMAND_RESET_ARG:
                return executeLoadConfigurationCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_CHANNELS_ARG:
                return executeChannelsCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_OFF_ARG:
                return executeShutDownCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_SEND_ARG:
                return executeSendMessageCommand(ArrayUtils.removeElement(args, args[0]), channelId, channelName);
            case COMMAND_7TV_ARG:
                return execute7TVCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_BTTV_ARG:
                return executeBTTVCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_FFZ_ARG:
                return executeFFZCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_EMOTES_ARG:
                return executeTwitchEmotesCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            default:
                return messageService.getMessageBuilder();
        }
    }

    private DefaultMessageServiceImpl.MessageBuilder executeSetFeatureCommand(final String[] args, final String channelId, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        if (args.length < 2) {
            return messageBuilder;
        }
        final Set<FeatureEnum> features;
        try {
            features = extractFeaturesFromCommandArg(args[0]);
        } catch (final Exception e) {
            return messageBuilder;
        }
        if (!Set.of(FeatureEnum.values()).containsAll(features)) {
            return messageBuilder;
        }
        switch (args[1]) {
            case FEATURE_COMMAND_ON_ARG:
                features.forEach(feature -> botFeatureService.setTwitchFeatureStatus(channelName, feature, true));
                return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, HAPPY))
                        .withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_DEFAULT + channelName.toLowerCase(), MESSAGE_COMMAND_DEFAULT + CHANNEL_DEFAULT));
            case FEATURE_COMMAND_OFF_ARG:
                features.forEach(feature -> botFeatureService.setTwitchFeatureStatus(channelName, feature, false));
                return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, HAPPY))
                        .withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_DEFAULT + channelName.toLowerCase(), MESSAGE_COMMAND_DEFAULT + CHANNEL_DEFAULT));
            default:
                return messageBuilder;
        }
    }

    private DefaultMessageServiceImpl.MessageBuilder executeStatusFeatureCommand(final String[] args, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        if (args.length != 1) {
            return messageBuilder;
        }
        final Set<FeatureEnum> features;
        try {
            features = extractFeaturesFromCommandArg(args[0]);
        } catch (final Exception e) {
            return messageBuilder;
        }
        if (!Set.of(FeatureEnum.values()).containsAll(features)) {
            return messageBuilder;
        }
        final StringBuilder responseTextBuilder = new StringBuilder();
        if (FEATURE_COMMAND_ALL_ARG.equalsIgnoreCase(args[0])) {
            responseTextBuilder.append(configurationService.getConfiguration(channelName).isMuted() ? "muted" : "unmuted").append(" | ");
        }
        features.forEach(feature -> {
            responseTextBuilder.append(feature)
                    .append(":")
                    .append(botFeatureService.isTwitchFeatureActive(channelName, FeatureEnum.valueOf(feature.toString().toUpperCase())) ? FEATURE_COMMAND_ON_ARG : FEATURE_COMMAND_OFF_ARG);
            responseTextBuilder.append(" | ");
        });
        return messageBuilder.withText(StringUtils.chop(responseTextBuilder.toString().trim()));
    }

    private Set<FeatureEnum> extractFeaturesFromCommandArg(final String arg) {
        return FEATURE_COMMAND_ALL_ARG.equalsIgnoreCase(arg) ? Set.of(FeatureEnum.values()) : Set.of(FeatureEnum.valueOf(arg.toUpperCase()));
    }

    private DefaultMessageServiceImpl.MessageBuilder executeMuteCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        return executeMuteUnmuteCommand(args, userName, channelId, channelName, true);
    }

    private DefaultMessageServiceImpl.MessageBuilder executeUnmuteCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        return executeMuteUnmuteCommand(args, userName, channelId, channelName, false);
    }

    private DefaultMessageServiceImpl.MessageBuilder executeMuteUnmuteCommand(final String[] args, final String userName, final String channelId, final String channelName, final boolean isMuted) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        if (args.length == 0) {
            configurationService.getConfiguration(channelName).setMuted(isMuted);
            return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, isMuted ? SAD : HAPPY))
                    .withText(isMuted
                            ? messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_MUTE + channelName.toLowerCase(), MESSAGE_COMMAND_MUTE + CHANNEL_DEFAULT)
                            : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNMUTE + channelName.toLowerCase(), MESSAGE_COMMAND_UNMUTE + CHANNEL_DEFAULT));
        } else if (args.length == 1) {
            if (COMMAND_ALL_ARG.equalsIgnoreCase(args[0])) {
                if (isSuperAdmin(userName)) {
                    configurationService.getConfiguration().getChannelConfigurations().values().forEach(configuration -> configuration.setMuted(isMuted));
                    return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, isMuted ? SAD : HAPPY))
                            .withText(isMuted
                                    ? messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_MUTE + channelName.toLowerCase(), MESSAGE_COMMAND_MUTE + CHANNEL_DEFAULT)
                                    : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNMUTE + channelName.toLowerCase(), MESSAGE_COMMAND_UNMUTE + CHANNEL_DEFAULT));
                } else {
                    return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, CONFUSION))
                            .withText(String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(), MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT)));
                }
            } else if (isChannelJoined(args[0].toLowerCase())) {
                if (isSuperAdmin(userName)) {
                    configurationService.getConfiguration(args[0].toLowerCase()).setMuted(isMuted);
                    return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, isMuted ? SAD : HAPPY))
                            .withText(isMuted
                                    ? messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_MUTE + channelName.toLowerCase(), MESSAGE_COMMAND_MUTE + CHANNEL_DEFAULT)
                                    : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNMUTE + channelName.toLowerCase(), MESSAGE_COMMAND_UNMUTE + CHANNEL_DEFAULT));
                } else {
                    return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, CONFUSION))
                            .withText(String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(),
                                    MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT)));
                }
            } else {
                return messageBuilder;
            }
        } else {
            return messageBuilder;
        }
    }

    private DefaultMessageServiceImpl.MessageBuilder executeShutDownCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        if (args.length > 0) {
            return messageBuilder;
        }
        if (isSuperAdmin(userName)) {
            try {
                return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, SAD))
                        .withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_SHUTDOWN + channelName.toLowerCase(), MESSAGE_COMMAND_SHUTDOWN + CHANNEL_DEFAULT));
            } finally {
                new Timer().schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                System.exit(-1);
                            }
                        },1000 // shut down in 1 second
                );
            }
        } else if (isBroadcaster(userName, channelName)) {
            return executeLeaveCommand(new String[0], userName, channelId, channelName);
        } else {
            return messageBuilder;
        }
    }

    private DefaultMessageServiceImpl.MessageBuilder executeJoinCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        if (args.length == 0) {
            return messageBuilder; // can't join current channel when bot is not here
        } else if (args.length == 1) {
            if (isSuperAdmin(userName)) {
                // todo validate channel name
                channelService.joinChannel(args[0].toLowerCase());
                return messageBuilder.withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_JOIN + channelName.toLowerCase(),
                        MESSAGE_COMMAND_JOIN + CHANNEL_DEFAULT) + StringUtils.SPACE + args[0].toLowerCase());
            } else {
                return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, CONFUSION))
                        .withText(String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(),
                                MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT)));
            }
        } else {
            return messageBuilder;
        }
    }

    private DefaultMessageServiceImpl.MessageBuilder executeLeaveCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        if (args.length == 0) {
            channelService.leaveChannel(channelName); // leave current channel
            return messageBuilder.withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_LEAVE + channelName.toLowerCase(),
                    MESSAGE_COMMAND_LEAVE + CHANNEL_DEFAULT) + StringUtils.SPACE + channelName);
        } else if (args.length == 1) {
            if (isSuperAdmin(userName)) {
                if (isChannelJoined(args[0].toLowerCase())) {
                    channelService.leaveChannel(args[0].toLowerCase());
                    return messageBuilder.withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_LEAVE + channelName.toLowerCase(),
                            MESSAGE_COMMAND_LEAVE + CHANNEL_DEFAULT) + StringUtils.SPACE + args[0].toLowerCase());
                } else {
                    return messageBuilder;
                }
            } else {
                return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, CONFUSION))
                        .withText(String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(),
                                MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT)));
            }
        } else {
            return messageBuilder;
        }
    }

    private DefaultMessageServiceImpl.MessageBuilder executeLoadConfigurationCommand(final String[] args, final String userName, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        if (args.length == 0) {
            configurationService.loadConfiguration(channelName);
            return messageBuilder.withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_LOAD_CONFIGURATION + channelName.toLowerCase(),
                    MESSAGE_COMMAND_LOAD_CONFIGURATION + CHANNEL_DEFAULT) + StringUtils.SPACE + channelName);
        } else if (args.length == 1) {
            if (isSuperAdmin(userName)) {
                if (isChannelJoined(args[0].toLowerCase())) {
                    configurationService.loadConfiguration(channelName);
                    return messageBuilder.withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_LOAD_CONFIGURATION + channelName.toLowerCase(),
                            MESSAGE_COMMAND_LOAD_CONFIGURATION + CHANNEL_DEFAULT) + StringUtils.SPACE + args[0].toLowerCase());
                } else {
                    return messageBuilder;
                }
            } else {
                return messageBuilder.withText(String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(),
                        MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT)));
            }
        } else {
            return messageBuilder;
        }
    }

    private boolean isChannelJoined(final String channelName) {
        return twitchClientService.getTwitchClient().getChat().getChannels().contains(channelName.toLowerCase());
    }

    private DefaultMessageServiceImpl.MessageBuilder executeChannelsCommand(final String[] args, final String userName, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        if (args.length > 0) {
            return messageBuilder;
        }
        if (isSuperAdmin(userName)) {
            final StringBuilder responseTextBuilder = new StringBuilder();
            twitchClientService.getTwitchClient().getChat().getChannels().forEach(channel -> {
                responseTextBuilder.append(channel).append(" | ");
            });
            return messageBuilder.withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_CHANNELS + channelName.toLowerCase(),
                    MESSAGE_COMMAND_CHANNELS + CHANNEL_DEFAULT) + StringUtils.SPACE + StringUtils.chop(responseTextBuilder.toString().trim()));
        } else {
            return messageBuilder.withText(String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(),
                    MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT)));
        }
    }

    private DefaultMessageServiceImpl.MessageBuilder executeSendMessageCommand(final String[] args, String channelId, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        if (args.length != 2) {
            return messageBuilder;
        }
        // todo validate channel name
        messageService.sendMessage(args[0].toLowerCase(), messageService.getMessageBuilder().withText(args[1]), null);
        return messageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 1, HAPPY))
                .withText(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_SEND + channelName.toLowerCase(), MESSAGE_COMMAND_SEND + CHANNEL_DEFAULT));
    }

    private DefaultMessageServiceImpl.MessageBuilder execute7TVCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        String emoteMessage = StringUtils.EMPTY;
        if (args.length == 1) {
            if (COMMAND_GLOBAL_ARG.equalsIgnoreCase(args[0])) {
                emoteMessage = twitchEmoteService.getGlobal7TVEmotes().stream().map(SevenTVEmote::getName).collect(Collectors.joining(StringUtils.SPACE));
            } else if (COMMAND_CHANNEL_ARG.equalsIgnoreCase(args[0])) {
                emoteMessage = twitchEmoteService.getChannel7TVEmotes(channelId).stream().map(SevenTVEmote::getName).collect(Collectors.joining(StringUtils.SPACE));
            }
        } else if (args.length == 0) {
            emoteMessage = twitchEmoteService.getChannel7TVEmotes(channelId).stream().map(SevenTVEmote::getName).collect(Collectors.joining(StringUtils.SPACE));
            emoteMessage += StringUtils.SPACE + twitchEmoteService.getGlobal7TVEmotes().stream().map(SevenTVEmote::getName).collect(Collectors.joining(StringUtils.SPACE));
        }
        return messageBuilder.withText(StringUtils.isNotEmpty(emoteMessage)
                ? emoteMessage
                : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_EMOTE_EMPTY + channelName.toLowerCase(), MESSAGE_COMMAND_EMOTE_EMPTY + CHANNEL_DEFAULT));
    }

    private DefaultMessageServiceImpl.MessageBuilder executeBTTVCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        String emoteMessage = StringUtils.EMPTY;
        if (args.length == 1) {
            if (COMMAND_GLOBAL_ARG.equalsIgnoreCase(args[0])) {
                emoteMessage = twitchEmoteService.getGlobalBTTVEmotes().stream().map(BTTVEmote::getCode).collect(Collectors.joining(StringUtils.SPACE));
            } else if (COMMAND_CHANNEL_ARG.equalsIgnoreCase(args[0])) {
                emoteMessage = twitchEmoteService.getChannelBTTVEmotes(channelId).stream().map(BTTVEmote::getCode).collect(Collectors.joining(StringUtils.SPACE));
            }
        } else if (args.length == 0) {
            emoteMessage = twitchEmoteService.getChannelBTTVEmotes(channelId).stream().map(BTTVEmote::getCode).collect(Collectors.joining(StringUtils.SPACE));
            emoteMessage += StringUtils.SPACE + twitchEmoteService.getGlobalBTTVEmotes().stream().map(BTTVEmote::getCode).collect(Collectors.joining(StringUtils.SPACE));
        }
        return messageBuilder.withText(StringUtils.isNotEmpty(emoteMessage)
                ? TAG_CHARACTER + userName + StringUtils.SPACE + emoteMessage
                : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_EMOTE_EMPTY + channelName.toLowerCase(), MESSAGE_COMMAND_EMOTE_EMPTY + CHANNEL_DEFAULT));
    }

    private DefaultMessageServiceImpl.MessageBuilder executeFFZCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        String emoteMessage = StringUtils.EMPTY;
        if (args.length == 1) {
            if (COMMAND_GLOBAL_ARG.equalsIgnoreCase(args[0])) {
                emoteMessage = twitchEmoteService.getGlobalFFZEmotes().stream().map(FFZEmoticon::getName).collect(Collectors.joining(StringUtils.SPACE));
            } else if (COMMAND_CHANNEL_ARG.equalsIgnoreCase(args[0])) {
                emoteMessage = twitchEmoteService.getChannelFFZEmotes(channelId).stream().map(FFZEmoticon::getName).collect(Collectors.joining(StringUtils.SPACE));
            }
        } else if (args.length == 0) {
            emoteMessage = twitchEmoteService.getChannelFFZEmotes(channelId).stream().map(FFZEmoticon::getName).collect(Collectors.joining(StringUtils.SPACE));
            emoteMessage += StringUtils.SPACE + twitchEmoteService.getGlobalFFZEmotes().stream().map(FFZEmoticon::getName).collect(Collectors.joining(StringUtils.SPACE));
        }
        return messageBuilder.withText(StringUtils.isNotEmpty(emoteMessage)
                ? TAG_CHARACTER + userName + StringUtils.SPACE + emoteMessage
                : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_EMOTE_EMPTY + channelName.toLowerCase(), MESSAGE_COMMAND_EMOTE_EMPTY + CHANNEL_DEFAULT));
    }

    private DefaultMessageServiceImpl.MessageBuilder executeTwitchEmotesCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final DefaultMessageServiceImpl.MessageBuilder messageBuilder = messageService.getMessageBuilder();
        String emoteMessage = StringUtils.EMPTY;
        if (args.length == 1) {
            if (COMMAND_GLOBAL_ARG.equalsIgnoreCase(args[0])) {
                emoteMessage = twitchEmoteService.getGlobalTwitchEmotes().stream().map(Emote::getName).collect(Collectors.joining(StringUtils.SPACE));
            } else if (COMMAND_CHANNEL_ARG.equalsIgnoreCase(args[0])) {
                emoteMessage = twitchEmoteService.getChannelTwitchEmotes(channelId).stream().map(Emote::getName).collect(Collectors.joining(StringUtils.SPACE));
            }
        } else if (args.length == 0) {
            emoteMessage = twitchEmoteService.getChannelTwitchEmotes(channelId).stream().map(Emote::getName).collect(Collectors.joining(StringUtils.SPACE));
            emoteMessage += StringUtils.SPACE + twitchEmoteService.getGlobalTwitchEmotes().stream().map(Emote::getName).collect(Collectors.joining(StringUtils.SPACE));
        }
        return messageBuilder.withText(StringUtils.isNotEmpty(emoteMessage)
                ? TAG_CHARACTER + userName + StringUtils.SPACE + emoteMessage
                : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_EMOTE_EMPTY + channelName.toLowerCase(), MESSAGE_COMMAND_EMOTE_EMPTY + CHANNEL_DEFAULT));
    }
}
