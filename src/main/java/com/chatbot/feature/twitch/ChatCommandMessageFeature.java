package com.chatbot.feature.twitch;

import com.chatbot.service.MessageService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.TwitchEmoteService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultTwitchClientServiceImpl;
import com.chatbot.service.impl.DefaultTwitchEmoteService;
import com.chatbot.util.FeatureEnum;
import com.chatbot.util.emotes.bttv.BTTVEmote;
import com.chatbot.util.emotes.ffz.FFZEmoticon;
import com.chatbot.util.emotes.seventv.SevenTVEmote;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.Emote;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

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

    private static final int MIN_PROBABILITY = 0;
    private static final int MAX_PROBABILITY = 100;

    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    private final TwitchEmoteService twitchEmoteService = DefaultTwitchEmoteService.getInstance();

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
            final String message = messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(), MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT);
            messageService.sendMessage(channelName, String.format(message, userName), false, null);
        }
        final String[] commandArgs = parseCommandArgs(event.getMessage());

        final String responseMessage = executeCommand(commandArgs, userName, channelId, channelName);
        if (StringUtils.isNotEmpty(responseMessage)) {
            messageService.sendMessage(channelName, String.format(responseMessage, userName), false, null);
        } else {
            final String errorMessage = messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_ERROR + channelName.toLowerCase(), MESSAGE_COMMAND_ERROR + CHANNEL_DEFAULT);
            messageService.sendMessage(channelName, String.format(errorMessage, userName), false, null);
        }
    }

    private boolean isSuperAdmin(final String userName) {
        return configurationService.getSuperAdminName().equalsIgnoreCase(userName);
    }

    private boolean isBroadcaster(final String userName, final String channelName) {
        return userName.equalsIgnoreCase(channelName);
    }

    private String[] parseCommandArgs(final String message) {
        final String commandContent = message.replace(COMMAND_SYNTAX, StringUtils.EMPTY).trim();
        if (StringUtils.isNotEmpty(commandContent) && commandContent.matches(COMMAND_SEND_ARG + "\\s[a-zA-Z_\\d]+\\s.+")) {
            final String[] channelAndMessageToSend = commandContent.replaceFirst(COMMAND_SEND_ARG, StringUtils.EMPTY).trim().split(StringUtils.SPACE, 2);
            return ArrayUtils.addAll(new String[]{COMMAND_SEND_ARG}, channelAndMessageToSend);
        }
        return commandContent.split(StringUtils.SPACE);
    }

    private String executeCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        if (args.length == 0) {
            return StringUtils.EMPTY;
        }
        switch (args[0].toLowerCase()) {
            case FEATURE_COMMAND_SET_ARG:
                return executeSetFeatureCommand(ArrayUtils.removeElement(args, args[0]), channelName);
            case FEATURE_COMMAND_STATUS_ARG:
                return executeStatusFeatureCommand(ArrayUtils.removeElement(args, args[0]), channelName);
            case COMMAND_MUTE_ARG:
                return executeMuteCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_UNMUTE_ARG:
                return executeUnmuteCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_JOIN_ARG:
                return executeJoinCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_LEAVE_ARG:
                return executeLeaveCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_LOAD_ARG:
            case COMMAND_RELOAD_ARG:
            case COMMAND_RESET_ARG:
                return executeLoadConfigurationCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_CHANNELS_ARG:
                return executeChannelsCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_OFF_ARG:
                return executeShutDownCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_SEND_ARG:
                return executeSendMessageCommand(ArrayUtils.removeElement(args, args[0]), channelName);
            case COMMAND_7TV_ARG:
                return execute7TVCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_BTTV_ARG:
                return executeBTTVCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_FFZ_ARG:
                return executeFFZCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            case COMMAND_EMOTES_ARG:
                return executeTwitchEmotesCommand(ArrayUtils.removeElement(args, args[0]), userName, channelId, channelName);
            default:
                return StringUtils.EMPTY;
        }
    }

    private String executeSetFeatureCommand(final String[] args, final String channelName) {
        if (args.length < 2) {
            return StringUtils.EMPTY;
        }
        final Set<FeatureEnum> features;
        try {
            features = extractFeaturesFromCommandArg(args[0]);
        } catch (final Exception e) {
            return StringUtils.EMPTY;
        }
        if (!Set.of(FeatureEnum.values()).containsAll(features)) {
            return StringUtils.EMPTY;
        }
        switch (args[1]) {
            case FEATURE_COMMAND_ON_ARG:
                features.forEach(feature -> botFeatureService.setTwitchFeatureStatus(channelName, feature, true));
                return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_DEFAULT + channelName.toLowerCase(), MESSAGE_COMMAND_DEFAULT + CHANNEL_DEFAULT);
            case FEATURE_COMMAND_OFF_ARG:
                features.forEach(feature -> botFeatureService.setTwitchFeatureStatus(channelName, feature, false));
                return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_DEFAULT + channelName.toLowerCase(), MESSAGE_COMMAND_DEFAULT + CHANNEL_DEFAULT);
            default:
                if (StringUtils.isNumeric(args[1])) {
                    int probability = NumberUtils.toInt(args[1]);
                    if (probability <= MIN_PROBABILITY) {
                        probability = MIN_PROBABILITY;
                    } else if (probability >= MAX_PROBABILITY) {
                        probability = MAX_PROBABILITY;
                    }
                    configurationService.getConfiguration(channelName).setIndependenceRate(probability);
                    return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_DEFAULT + channelName.toLowerCase(), MESSAGE_COMMAND_DEFAULT + CHANNEL_DEFAULT);
                }
                return StringUtils.EMPTY;
        }
    }

    private String executeStatusFeatureCommand(final String[] args, final String channelName) {
        if (args.length != 1) {
            return StringUtils.EMPTY;
        }
        final Set<FeatureEnum> features;
        try {
            features = extractFeaturesFromCommandArg(args[0]);
        } catch (final Exception e) {
            return StringUtils.EMPTY;
        }
        if (!Set.of(FeatureEnum.values()).containsAll(features)) {
            return StringUtils.EMPTY;
        }
        final StringBuilder messageBuilder = new StringBuilder("%s ");
        if (FEATURE_COMMAND_ALL_ARG.equalsIgnoreCase(args[0])) {
            messageBuilder.append(configurationService.getConfiguration(channelName).isMuted() ? "muted" : "unmuted").append(" | ");
        }
        features.forEach(feature -> {
            messageBuilder.append(feature)
                    .append(":")
                    .append(botFeatureService.isTwitchFeatureActive(channelName, FeatureEnum.valueOf(feature.toString().toUpperCase())) ? FEATURE_COMMAND_ON_ARG : FEATURE_COMMAND_OFF_ARG);
            if (FeatureEnum.ALIVE.equals(feature)) {
                messageBuilder.append(":").append(configurationService.getConfiguration(channelName).getIndependenceRate());
            }
            messageBuilder.append(" | ");
        });
        return StringUtils.chop(messageBuilder.toString().trim());
    }

    private Set<FeatureEnum> extractFeaturesFromCommandArg(final String arg) {
        return FEATURE_COMMAND_ALL_ARG.equalsIgnoreCase(arg) ? Set.of(FeatureEnum.values()) : Set.of(FeatureEnum.valueOf(arg.toUpperCase()));
    }

    private String executeMuteCommand(final String[] args, final String userName, final String channelName) {
        return executeMuteUnmuteCommand(args, userName, channelName, true);
    }

    private String executeUnmuteCommand(final String[] args, final String userName, final String channelName) {
        return executeMuteUnmuteCommand(args, userName, channelName, false);
    }

    private String executeMuteUnmuteCommand(final String[] args, final String userName, final String channelName, final boolean isMuted) {
        if (args.length == 0) {
            configurationService.getConfiguration(channelName).setMuted(isMuted);
            return isMuted
                    ? messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_MUTE + channelName.toLowerCase(), MESSAGE_COMMAND_MUTE + CHANNEL_DEFAULT)
                    : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNMUTE + channelName.toLowerCase(), MESSAGE_COMMAND_UNMUTE + CHANNEL_DEFAULT);
        } else if (args.length == 1) {
            if (COMMAND_ALL_ARG.equalsIgnoreCase(args[0])) {
                if (isSuperAdmin(userName)) {
                    configurationService.getConfiguration().getChannelConfigurations().values().forEach(configuration -> configuration.setMuted(isMuted));
                    return isMuted
                            ? messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_MUTE + channelName.toLowerCase(), MESSAGE_COMMAND_MUTE + CHANNEL_DEFAULT)
                            : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNMUTE + channelName.toLowerCase(), MESSAGE_COMMAND_UNMUTE + CHANNEL_DEFAULT);
                } else {
                    return String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(), MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT));
                }
            } else if (isChannelJoined(args[0].toLowerCase())) {
                if (isSuperAdmin(userName)) {
                    configurationService.getConfiguration(args[0].toLowerCase()).setMuted(isMuted);
                    return isMuted
                            ? messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_MUTE + channelName.toLowerCase(), MESSAGE_COMMAND_MUTE + CHANNEL_DEFAULT)
                            : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNMUTE + channelName.toLowerCase(), MESSAGE_COMMAND_UNMUTE + CHANNEL_DEFAULT);
                } else {
                    return String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(), MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT));
                }
            } else {
                return StringUtils.EMPTY;
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String executeShutDownCommand(final String[] args, final String userName, final String channelName) {
        if (args.length > 0) {
            return StringUtils.EMPTY;
        }
        if (isSuperAdmin(userName)) {
            try {
                return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_SHUTDOWN + channelName.toLowerCase(), MESSAGE_COMMAND_SHUTDOWN + CHANNEL_DEFAULT);
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
            return executeLeaveCommand(new String[0], userName, channelName);
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String executeJoinCommand(final String[] args, final String userName, final String channelName) {
        if (args.length == 0) {
            return StringUtils.EMPTY; // can't join current channel when bot is not here
        } else if (args.length == 1) {
            if (isSuperAdmin(userName)) {
                // todo validate channel name
                channelService.joinChannel(args[0].toLowerCase());
                return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_JOIN + channelName.toLowerCase(), MESSAGE_COMMAND_JOIN + CHANNEL_DEFAULT) + StringUtils.SPACE + args[0].toLowerCase();
            } else {
                return String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(), MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT));
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String executeLeaveCommand(final String[] args, final String userName, final String channelName) {
        if (args.length == 0) {
            channelService.leaveChannel(channelName); // leave current channel
            return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_LEAVE + channelName.toLowerCase(), MESSAGE_COMMAND_LEAVE + CHANNEL_DEFAULT) + StringUtils.SPACE + channelName;
        } else if (args.length == 1) {
            if (isSuperAdmin(userName)) {
                if (isChannelJoined(args[0].toLowerCase())) {
                    channelService.leaveChannel(args[0].toLowerCase());
                    return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_LEAVE + channelName.toLowerCase(), MESSAGE_COMMAND_LEAVE + CHANNEL_DEFAULT) + StringUtils.SPACE + args[0].toLowerCase();
                } else {
                    return StringUtils.EMPTY;
                }
            } else {
                return String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(), MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT));
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String executeLoadConfigurationCommand(final String[] args, final String userName, final String channelName) {
        if (args.length == 0) {
            configurationService.loadConfiguration(channelName);
            return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_LOAD_CONFIGURATION + channelName.toLowerCase(), MESSAGE_COMMAND_LOAD_CONFIGURATION + CHANNEL_DEFAULT) + StringUtils.SPACE + channelName;
        } else if (args.length == 1) {
            if (isSuperAdmin(userName)) {
                if (isChannelJoined(args[0].toLowerCase())) {
                    configurationService.loadConfiguration(channelName);
                    return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_LOAD_CONFIGURATION + channelName.toLowerCase(), MESSAGE_COMMAND_LOAD_CONFIGURATION + CHANNEL_DEFAULT) + StringUtils.SPACE + args[0].toLowerCase();
                } else {
                    return StringUtils.EMPTY;
                }
            } else {
                return String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(), MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT));
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    private boolean isChannelJoined(final String channelName) {
        return twitchClientService.getTwitchClient().getChat().getChannels().contains(channelName.toLowerCase());
    }

    private String executeChannelsCommand(final String[] args, final String userName, final String channelName) {
        if (args.length > 0) {
            return StringUtils.EMPTY;
        }
        if (isSuperAdmin(userName)) {
            final StringBuilder messageBuilder = new StringBuilder();
            twitchClientService.getTwitchClient().getChat().getChannels().forEach(channel -> {
                messageBuilder.append(channel).append(" | ");
            });
            return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_CHANNELS + channelName.toLowerCase(), MESSAGE_COMMAND_CHANNELS + CHANNEL_DEFAULT) + StringUtils.SPACE + StringUtils.chop(messageBuilder.toString().trim());
        } else {
            return String.format(messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED + channelName.toLowerCase(), MESSAGE_COMMAND_UNAUTHORIZED + CHANNEL_DEFAULT));
        }
    }

    private String executeSendMessageCommand(final String[] args, final String channelName) {
        if (args.length != 2) {
            return StringUtils.EMPTY;
        }
        // todo validate channel name
        messageService.sendMessage(args[0].toLowerCase(), args[1], null);
        return messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_SEND + channelName.toLowerCase(), MESSAGE_COMMAND_SEND + CHANNEL_DEFAULT);
    }

    private String execute7TVCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final String emoteMessage;
        if (args.length == 1 && COMMAND_GLOBAL_ARG.equalsIgnoreCase(args[0])) {
            emoteMessage = twitchEmoteService.getGlobal7TVEmotes().stream().map(SevenTVEmote::getName).collect(Collectors.joining(StringUtils.SPACE));
        } else {
            emoteMessage = twitchEmoteService.getChannel7TVEmotes(channelId).stream().map(SevenTVEmote::getName).collect(Collectors.joining(StringUtils.SPACE));
        }
        return StringUtils.isNotEmpty(emoteMessage)
                ? TAG_CHARACTER + userName + StringUtils.SPACE + emoteMessage
                : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_EMOTE_EMPTY + channelName.toLowerCase(), MESSAGE_COMMAND_EMOTE_EMPTY + CHANNEL_DEFAULT);
    }

    private String executeBTTVCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final String emoteMessage;
        if (args.length == 1 && COMMAND_GLOBAL_ARG.equalsIgnoreCase(args[0])) {
            emoteMessage = twitchEmoteService.getGlobalBTTVEmotes().stream().map(BTTVEmote::getCode).collect(Collectors.joining(StringUtils.SPACE));
        } else {
            emoteMessage = twitchEmoteService.getChannelBTTVEmotes(channelId).stream().map(BTTVEmote::getCode).collect(Collectors.joining(StringUtils.SPACE));
        }
        return StringUtils.isNotEmpty(emoteMessage)
                ? TAG_CHARACTER + userName + StringUtils.SPACE + emoteMessage
                : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_EMOTE_EMPTY + channelName.toLowerCase(), MESSAGE_COMMAND_EMOTE_EMPTY + CHANNEL_DEFAULT);
    }

    private String executeFFZCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final String emoteMessage;
        if (args.length == 1 && COMMAND_GLOBAL_ARG.equalsIgnoreCase(args[0])) {
            emoteMessage = twitchEmoteService.getGlobalFFZEmotes().stream().map(FFZEmoticon::getName).collect(Collectors.joining(StringUtils.SPACE));
        } else {
            emoteMessage = twitchEmoteService.getChannelFFZEmotes(channelId).stream().map(FFZEmoticon::getName).collect(Collectors.joining(StringUtils.SPACE));
        }
        return StringUtils.isNotEmpty(emoteMessage)
                ? TAG_CHARACTER + userName + StringUtils.SPACE + emoteMessage
                : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_EMOTE_EMPTY + channelName.toLowerCase(), MESSAGE_COMMAND_EMOTE_EMPTY + CHANNEL_DEFAULT);
    }

    private String executeTwitchEmotesCommand(final String[] args, final String userName, final String channelId, final String channelName) {
        final String emoteMessage;
        if (args.length == 1 && COMMAND_GLOBAL_ARG.equalsIgnoreCase(args[0])) {
            emoteMessage = twitchEmoteService.getGlobalTwitchEmotes().stream().map(Emote::getName).collect(Collectors.joining(StringUtils.SPACE));
        } else {
            emoteMessage = twitchEmoteService.getChannelTwitchEmotes(channelId).stream().map(Emote::getName).collect(Collectors.joining(StringUtils.SPACE));
        }
        return StringUtils.isNotEmpty(emoteMessage)
                ? TAG_CHARACTER + userName + StringUtils.SPACE + emoteMessage
                : messageService.getPersonalizedMessageForKey(MESSAGE_COMMAND_EMOTE_EMPTY + channelName.toLowerCase(), MESSAGE_COMMAND_EMOTE_EMPTY + CHANNEL_DEFAULT);
    }
}
