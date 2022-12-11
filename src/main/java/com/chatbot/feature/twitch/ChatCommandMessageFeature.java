package com.chatbot.feature.twitch;

import com.chatbot.service.MessageService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultTwitchClientServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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

    private static final String MESSAGE_COMMAND_DEFAULT = "message.command.default";
    private static final String MESSAGE_COMMAND_UNAUTHORIZED = "message.command.unauthorized";
    private static final String MESSAGE_COMMAND_MUTE = "message.command.mute";
    private static final String MESSAGE_COMMAND_UNMUTE = "message.command.unmute";
    private static final String MESSAGE_COMMAND_LOAD_CONFIGURATION = "message.command.load.configuration";

    private static final int MIN_PROBABILITY = 0;
    private static final int MAX_PROBABILITY = 100;

    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    public ChatCommandMessageFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        if (!isCommand(event.getMessage())) {
            return;
        }
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isSuperAdmin(userName) && !isBroadcaster(userName, event.getChannel().getName())) {
            messageService.sendMessage(channelName, String.format(messageService.getStandardMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED), userName), false, null);
        }
        final String[] commandArgs = parseCommandArgs(event.getMessage());

        final String responseMessage = executeCommand(commandArgs, userName, channelName);
        if (StringUtils.isNotEmpty(responseMessage)) {
            messageService.sendMessage(channelName, String.format(responseMessage, userName), false, null);
        } else {
            messageService.sendMessage(channelName, String.format(messageService.getStandardMessageForKey("message.command.error"), userName), false, null);
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

    private String executeCommand(final String[] args, final String userName, final String channelName) {
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
                return executeJoinCommand(ArrayUtils.removeElement(args, args[0]), userName);
            case COMMAND_LEAVE_ARG:
                return executeLeaveCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_LOAD_ARG:
            case COMMAND_RELOAD_ARG:
            case COMMAND_RESET_ARG:
                return executeLoadConfigurationCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_CHANNELS_ARG:
                return executeChannelsCommand(ArrayUtils.removeElement(args, args[0]), userName);
            case COMMAND_OFF_ARG:
                return executeShutDownCommand(ArrayUtils.removeElement(args, args[0]), userName, channelName);
            case COMMAND_SEND_ARG:
                return executeSendMessageCommand(ArrayUtils.removeElement(args, args[0]));
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
                return messageService.getStandardMessageForKey(MESSAGE_COMMAND_DEFAULT);
            case FEATURE_COMMAND_OFF_ARG:
                features.forEach(feature -> botFeatureService.setTwitchFeatureStatus(channelName, feature, false));
                return messageService.getStandardMessageForKey(MESSAGE_COMMAND_DEFAULT);
            default:
                if (StringUtils.isNumeric(args[1])) {
                    int probability = NumberUtils.toInt(args[1]);
                    if (probability <= MIN_PROBABILITY) {
                        probability = MIN_PROBABILITY;
                    } else if (probability >= MAX_PROBABILITY) {
                        probability = MAX_PROBABILITY;
                    }
                    configurationService.getConfiguration(channelName).setIndependenceRate(probability);
                    return messageService.getStandardMessageForKey(MESSAGE_COMMAND_DEFAULT);
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
            return messageService.getStandardMessageForKey(isMuted ? MESSAGE_COMMAND_MUTE : MESSAGE_COMMAND_UNMUTE);
        } else if (args.length == 1) {
            if (COMMAND_ALL_ARG.equalsIgnoreCase(args[0])) {
                if (isSuperAdmin(userName)) {
                    configurationService.getConfiguration().getChannelConfigurations().values().forEach(configuration -> configuration.setMuted(isMuted));
                    return messageService.getStandardMessageForKey(isMuted ? MESSAGE_COMMAND_MUTE : MESSAGE_COMMAND_UNMUTE);
                } else {
                    return String.format(messageService.getStandardMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED));
                }
            } else if (isChannelJoined(args[0].toLowerCase())) {
                if (isSuperAdmin(userName)) {
                    configurationService.getConfiguration(args[0].toLowerCase()).setMuted(isMuted);
                    return messageService.getStandardMessageForKey(isMuted ? MESSAGE_COMMAND_MUTE : MESSAGE_COMMAND_UNMUTE);
                } else {
                    return String.format(messageService.getStandardMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED));
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
                return messageService.getStandardMessageForKey("message.command.shutdown");
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

    private String executeJoinCommand(final String[] args, final String userName) {
        if (args.length == 0) {
            return StringUtils.EMPTY; // can't join current channel when bot is not here
        } else if (args.length == 1) {
            if (isSuperAdmin(userName)) {
                // todo validate channel name
                channelService.joinChannel(args[0].toLowerCase());
                return messageService.getStandardMessageForKey("message.command.join") + StringUtils.SPACE + args[0].toLowerCase();
            } else {
                return String.format(messageService.getStandardMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED));
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String executeLeaveCommand(final String[] args, final String userName, final String channelName) {
        if (args.length == 0) {
            channelService.leaveChannel(channelName); // leave current channel
            return messageService.getStandardMessageForKey("message.command.leave") + StringUtils.SPACE + channelName;
        } else if (args.length == 1) {
            if (isSuperAdmin(userName)) {
                if (isChannelJoined(args[0].toLowerCase())) {
                    channelService.leaveChannel(args[0].toLowerCase());
                    return messageService.getStandardMessageForKey("message.command.leave") + StringUtils.SPACE + args[0].toLowerCase();
                } else {
                    return StringUtils.EMPTY;
                }
            } else {
                return String.format(messageService.getStandardMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED));
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String executeLoadConfigurationCommand(final String[] args, final String userName, final String channelName) {
        if (args.length == 0) {
            configurationService.loadConfiguration(channelName);
            return messageService.getStandardMessageForKey(MESSAGE_COMMAND_LOAD_CONFIGURATION) + StringUtils.SPACE + channelName;
        } else if (args.length == 1) {
            if (isSuperAdmin(userName)) {
                if (isChannelJoined(args[0].toLowerCase())) {
                    configurationService.loadConfiguration(channelName);
                    return messageService.getStandardMessageForKey(MESSAGE_COMMAND_LOAD_CONFIGURATION) + StringUtils.SPACE + args[0].toLowerCase();
                } else {
                    return StringUtils.EMPTY;
                }
            } else {
                return String.format(messageService.getStandardMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED));
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    private boolean isChannelJoined(final String channelName) {
        return twitchClientService.getTwitchClient().getChat().getChannels().contains(channelName.toLowerCase());
    }

    private String executeChannelsCommand(final String[] args, final String userName) {
        if (args.length > 0) {
            return StringUtils.EMPTY;
        }
        if (isSuperAdmin(userName)) {
            final StringBuilder messageBuilder = new StringBuilder();
            twitchClientService.getTwitchClient().getChat().getChannels().forEach(channel -> {
                messageBuilder.append(channel).append(" | ");
            });
            return messageService.getStandardMessageForKey("message.command.channels") + StringUtils.SPACE + StringUtils.chop(messageBuilder.toString().trim());
        } else {
            return String.format(messageService.getStandardMessageForKey(MESSAGE_COMMAND_UNAUTHORIZED));
        }
    }

    private String executeSendMessageCommand(final String[] args) {
        if (args.length != 2) {
            return StringUtils.EMPTY;
        }
        // todo validate channel name
        messageService.sendMessage(args[0].toLowerCase(), args[1], null);
        return messageService.getStandardMessageForKey("message.command.send");
    }
}
