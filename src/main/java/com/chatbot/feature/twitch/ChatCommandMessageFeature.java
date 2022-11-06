package com.chatbot.feature.twitch;

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
    private static final String FEATURE_COMMAND_ARG = "-f";
    private static final String FEATURE_COMMAND_ALL_ARG = "all";
    private static final String FEATURE_COMMAND_ON_ARG = "on";
    private static final String FEATURE_COMMAND_OFF_ARG = "off";
    private static final String FEATURE_COMMAND_STATUS_ARG = "status";

    private static final String COMMAND_MUTE = "mute";
    private static final String COMMAND_UNMUTE = "unmute";

    private static final String COMMAND_OFF = "off";

    private static final int MIN_PROBABILITY = 0;
    private static final int MAX_PROBABILITY = 100;

    public ChatCommandMessageFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final String message = event.getMessage();
        if (!isCommand(message)) {
            return;
        }
        if (!isSuperAdmin(userName) && !isChannelOwner(userName, event.getChannel().getName())) {
            messageService.sendMessage(channelName, String.format(messageService.getStandardMessageForKey("message.command.unauthorized"), userName), false);
        }
        final String[] commandArgs = parseCommandArgs(message);

        final String responseMessage = executeCommand(commandArgs);
        if (StringUtils.isNotEmpty(responseMessage)) {
            messageService.sendMessage(channelName, String.format(responseMessage, userName), false);
        } else {
            messageService.sendMessage(channelName, String.format(messageService.getStandardMessageForKey("message.command.error"), userName), false);
        }
    }

    private boolean isSuperAdmin(final String userName) {
        return configurationService.getSuperAdminName().equalsIgnoreCase(userName);
    }

    private boolean isChannelOwner(final String userName, final String channelName) {
        return userName.equalsIgnoreCase(channelName);
    }

    private String[] parseCommandArgs(final String message) {
        return message.replace(getCommandSyntax(), StringUtils.EMPTY).trim().split(StringUtils.SPACE);
    }

    private String executeCommand(final String[] args) {
        if (args.length == 0) {
            return StringUtils.EMPTY;
        }
        switch (args[0].toLowerCase()) {
            case FEATURE_COMMAND_ARG:
                return executeFeatureCommand(ArrayUtils.removeElement(args, args[0]));
            case COMMAND_MUTE:
                return executeMuteCommand();
            case COMMAND_UNMUTE:
                return executeUnmuteCommand();
            case COMMAND_OFF:
                return executeShutDownCommand();
            default:
                return StringUtils.EMPTY;
        }
    }

    private String executeFeatureCommand(final String[] args) {
        if (args.length < 2) {
            return StringUtils.EMPTY;
        }
        final Set<FeatureEnum> features;
        if (FEATURE_COMMAND_ALL_ARG.equalsIgnoreCase(args[0])) {
            features = Set.of(FeatureEnum.values());
        } else {
            try {
                features = Set.of(FeatureEnum.valueOf(args[0].toUpperCase()));
            } catch (final Exception e) {
                return StringUtils.EMPTY;
            }
        }
        if (!Set.of(FeatureEnum.values()).containsAll(features)) {
            return StringUtils.EMPTY;
        }
        switch (args[1]) {
            case FEATURE_COMMAND_ON_ARG:
                features.forEach(feature -> botFeatureService.setTwitchFeatureStatus(feature, true));
                return messageService.getStandardMessageForKey("message.command.default");
            case FEATURE_COMMAND_OFF_ARG:
                features.forEach(feature -> botFeatureService.setTwitchFeatureStatus(feature, false));
                return messageService.getStandardMessageForKey("message.command.default");
            case FEATURE_COMMAND_STATUS_ARG:
                final StringBuilder messageBuilder = new StringBuilder("%s ");
                features.forEach(feature -> {
                    messageBuilder.append(feature)
                            .append(":")
                            .append(botFeatureService.isTwitchFeatureActive(FeatureEnum.valueOf(feature.toString().toUpperCase())) ? FEATURE_COMMAND_ON_ARG : FEATURE_COMMAND_OFF_ARG);
                    if (FeatureEnum.ALIVE.equals(feature)) {
                        messageBuilder.append(":").append(botFeatureService.getRandomAnswerProbability());
                    }
                    messageBuilder.append(" | ");
                });
                return StringUtils.chop(messageBuilder.toString().trim());
            default:
                if (StringUtils.isNumeric(args[1])) {
                    int probability = NumberUtils.toInt(args[1]);
                    if (probability <= MIN_PROBABILITY) {
                        probability = MIN_PROBABILITY;
                    } else if (probability >= MAX_PROBABILITY) {
                        probability = MAX_PROBABILITY;
                    }
                    botFeatureService.setRandomAnswerProbability(probability);
                    return messageService.getStandardMessageForKey("message.command.default");
                }
                return StringUtils.EMPTY;
        }
    }

    private String executeMuteCommand() {
        botFeatureService.setMuted(true);
        return messageService.getStandardMessageForKey("message.command.mute");
    }

    private String executeUnmuteCommand() {
        botFeatureService.setMuted(false);
        return messageService.getStandardMessageForKey("message.command.unmute");
    }

    private String executeShutDownCommand() {
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
    }
}
