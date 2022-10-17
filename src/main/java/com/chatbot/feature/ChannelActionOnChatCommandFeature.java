package com.chatbot.feature;

import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class ChannelActionOnChatCommandFeature extends AbstractFeature {
    private static final String SUPER_ADMIN_NAME = "0mskbird";
    private static final String FEATURE_COMMAND_ARG = "-f";
    private static final String FEATURE_COMMAND_ALL_ARG = "all";
    private static final String FEATURE_COMMAND_ON_ARG = "on";
    private static final String FEATURE_COMMAND_OFF_ARG = "off";
    private static final String FEATURE_COMMAND_STATUS_ARG = "status";

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    public ChannelActionOnChatCommandFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String userName = event.getUser().getName();
        final String message = event.getMessage();
        if (!isCommand(message)) {
            return;
        }
        if (!isSuperAdmin(userName)) {
            messageService.respond(event, String.format(messageService.getStandardMessageForKey("message.command.no.permission"), userName));
        }
        final String[] commandArgs = parseCommandArgs(message);

        final String responseMessage = executeCommand(commandArgs);
        if (StringUtils.isNotEmpty(responseMessage)) {
            messageService.respond(event, String.format(responseMessage, userName));
        } else {
            messageService.respond(event, String.format(messageService.getStandardMessageForKey("message.command.error"), userName));
        }
    }

    private boolean isSuperAdmin(final String channelName) {
        return SUPER_ADMIN_NAME.equalsIgnoreCase(channelName);
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
                features.forEach(feature -> botFeatureService.setFeatureStatus(feature, true));
                return messageService.getStandardMessageForKey("message.command.default");
            case FEATURE_COMMAND_OFF_ARG:
                features.forEach(feature -> botFeatureService.setFeatureStatus(feature, false));
                return messageService.getStandardMessageForKey("message.command.default");
            case FEATURE_COMMAND_STATUS_ARG:
                final StringBuilder messageBuilder = new StringBuilder("%s ");
                features.forEach(feature -> messageBuilder.append(feature)
                        .append(":")
                        .append(botFeatureService.isFeatureActive(FeatureEnum.valueOf(feature.toString().toUpperCase())) ? FEATURE_COMMAND_ON_ARG : FEATURE_COMMAND_OFF_ARG)
                        .append(" | "));
                return StringUtils.chop(messageBuilder.toString().trim());
            default:
                return StringUtils.EMPTY;
        }
    }
}
