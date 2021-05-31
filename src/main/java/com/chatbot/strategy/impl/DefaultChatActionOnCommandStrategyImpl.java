package com.chatbot.strategy.impl;

import com.chatbot.entity.command.BotCommandConfigurationEntity;
import com.chatbot.entity.command.BotCommandSimpleResponseActionEntity;
import com.chatbot.service.BotCommandService;
import com.chatbot.service.ChannelService;
import com.chatbot.service.impl.DefaultBotCommandServiceImpl;
import com.chatbot.service.impl.DefaultChannelServiceImpl;
import com.chatbot.strategy.CommandActionStrategy;
import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.service.BotFeatureService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultTwitchClientServiceImpl;
import com.chatbot.strategy.ChatResponseStrategy;
import com.chatbot.util.TechnicalBotCommandTriggerEnum;
import com.chatbot.util.FeatureEnum;
import com.github.twitch4j.helix.domain.StreamList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chatbot.util.TechnicalBotCommandTriggerEnum.COMMANDS;
import static com.chatbot.util.TechnicalBotCommandTriggerEnum.FEATURE;
import static com.chatbot.util.CommandUtils.COMMAND_SIGN;
import static com.chatbot.util.MessageUtils.USER_TAG;

public class DefaultChatActionOnCommandStrategyImpl extends AbstractResponseStrategy implements ChatResponseStrategy {
    private static DefaultChatActionOnCommandStrategyImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultChatActionOnCommandStrategyImpl.class);

    private static final String MESSAGE_STREAM_OFFLINE_DEFAULT = "message.stream.offline.default";
    private static final String MESSAGE_FEATURE_WRONG_COMMAND_SYNTAX_DEFAULT = "message.feature.wrong.command.syntax.default";
    private static final String MESSAGE_COMMAND_NO_PERMISSION_DEFAULT = "message.command.no.permission.default";

    private static final String FEATURE_COMMAND_INFO_ARG = "info";
    private static final String FEATURE_COMMAND_ENABLE_ARG = "enable";
    private static final String FEATURE_COMMAND_DISABLE_ARG = "disable";

    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    private final BotCommandService botCommandService = DefaultBotCommandServiceImpl.getInstance();
    private final ChannelService channelService = DefaultChannelServiceImpl.getInstance();

    private final CommandActionStrategy simpleResponseCommandStrategy = DefaultSimpleResponseActionStrategyImpl.getInstance();

    private DefaultChatActionOnCommandStrategyImpl() {
    }

    public static synchronized DefaultChatActionOnCommandStrategyImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChatActionOnCommandStrategyImpl();
        }
        return instance;
    }

    @Override
    public void respond(final AbstractChannelEvent abstractEvent) {
        final ChannelMessageEvent event = ((ChannelMessageEvent) abstractEvent);
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();

        final Optional<TechnicalBotCommandTriggerEnum> extractedTechnicalCommandTriggerOptional = botCommandService.extractTechnicalCommandTrigger(channelName, event.getMessage());
        if (extractedTechnicalCommandTriggerOptional.isPresent()) {
            respondOnTechnicalCommand(event, extractedTechnicalCommandTriggerOptional.get());
            return;
        }
        String responseMessage = StringUtils.EMPTY;

        final Optional<BotCommandConfigurationEntity> botCommandConfigurationEntityOptional = botCommandService.getCommandForChannelAndMessage(channelName, event.getMessage());
        botCommandConfigurationEntityOptional.ifPresent(botCommandConfigurationEntity -> Optional.ofNullable(botCommandConfigurationEntity.getConfiguredCommandActions())
                .orElse(Collections.emptyList())
                .forEach(botCommandActionEntity -> {
                    if (botCommandActionEntity instanceof BotCommandSimpleResponseActionEntity) {
                        simpleResponseCommandStrategy.execute(event, botCommandActionEntity);
                    }
                }));
        //responseMessage = buildResponseMessageOnCommand(botCommandEntity, event);
        // todo implement command strategies on custom commands
        respond(event, responseMessage, userName);
    }

    private void respondOnTechnicalCommand(final ChannelMessageEvent event, final TechnicalBotCommandTriggerEnum technicalCommand) {
        final String userName = event.getUser().getName();
        final String channelName = event.getChannel().getName();
        String responseMessage = StringUtils.EMPTY;
        switch (technicalCommand) {
            case COMMANDS:
                responseMessage = String.format(messageService.getStandardMessageForCommand(COMMANDS), USER_TAG + userName,
                        getAvailableCommandTriggersForUser(channelName, userName).stream()
                                .map(commandName -> COMMAND_SIGN + commandName)
                                .collect(Collectors.joining(", ")));
                break;
            case FEATURE:
                if (!channelService.isUserSuperAdmin(userName)) {
                    responseMessage = String.format(messageService.getStandardMessageForKey(MESSAGE_COMMAND_NO_PERMISSION_DEFAULT), userName);
                } else {
                    responseMessage = executeCommandFeatureAndBuildResponseMessage(event);
                }
                break;
        }
        respond(event, responseMessage, userName);
    }

    private Set<String> getAvailableCommandTriggersForUser(final String channelName, final String userName) {
        final Set<String> allTriggers = botCommandService.getAllCommandTriggersForChannel(channelName);
        allTriggers.add(COMMANDS.toString());
        if (channelService.isUserSuperAdmin(userName)) {
            allTriggers.addAll(Arrays.stream(TechnicalBotCommandTriggerEnum.values()).map(TechnicalBotCommandTriggerEnum::toString).collect(Collectors.toSet()));
        }
        return allTriggers;
    }

    /*private String buildResponseMessageOnCommand(final BotCommandEntity botCommandEntity, final ChannelMessageEvent event) {
        final String message = event.getMessage();
        final String userName = event.getUser().getName();
        final TechnicalBotCommandTriggerEnum command = extractCommandPart(message);
        switch (command) {
            case TEST:
                return String.format(messageService.getStandardMessageForCommand(TEST), USER_TAG + userName, new Date().getTime());
            case COMMANDS:
                return String.format(messageService.getStandardMessageForCommand(COMMANDS), USER_TAG + userName,
                        Arrays.stream(TechnicalBotCommandTriggerEnum.values()).map(commandName -> COMMAND_SIGN + commandName.toString()).collect(Collectors.joining(", ")));
            case TIME:
                final Optional<Stream> streamOptional = getStream(event.getChannel().getName());
                return streamOptional.map(stream -> String.format(messageService.getStandardMessageForCommand(TechnicalBotCommandTriggerEnum.TIME),
                        USER_TAG + userName, getCurrentStreamUpTime(stream.getStartedAtInstant()), stream.getGameName()))
                        .orElse(String.format(messageService.getStandardMessageForKey(MESSAGE_STREAM_OFFLINE_DEFAULT), USER_TAG + userName));
            case FEATURE:
                if (!globalConfigurationService.getSuperAdminName().equalsIgnoreCase(userName)) {
                    return String.format(messageService.getStandardMessageForKey(MESSAGE_COMMAND_NO_PERMISSION_DEFAULT), userName);
                }
                return buildResponseMessageAndExecuteCommandFeature(event);
            default:
                return StringUtils.EMPTY;
        }
    }*/

    private Optional<com.github.twitch4j.helix.domain.Stream> getStream(final String channelName) {
        final StreamList resultList = twitchClientService.getTwitchClient().getHelix()
                .getStreams(getAuthToken(), null, null, 1, null, null, null,  Collections.singletonList(channelName))
                .execute();
        return resultList.getStreams().stream().findFirst();
    }

    private String getAuthToken() {
        return staticConfigurationService.getStaticConfiguration().getCredentials().get("access_token");
    }

    private String executeCommandFeatureAndBuildResponseMessage(final ChannelMessageEvent event) {
        final String userName = event.getUser().getName();
        final String channelName = event.getChannel().getName();
        final String message = event.getMessage();
        final String[] arguments = message.split(" ");
        if (!(COMMAND_SIGN + FEATURE).equalsIgnoreCase(arguments[0]) || arguments.length < 2) {
            return String.format(messageService.getStandardMessageForKey(MESSAGE_FEATURE_WRONG_COMMAND_SYNTAX_DEFAULT), USER_TAG + userName);
        }
        switch (arguments[1]) {
            case FEATURE_COMMAND_INFO_ARG:
                return String.format(messageService.getStandardMessageForCommand(FEATURE), USER_TAG + userName, getActiveFeatureListForChannel(channelName));
            case FEATURE_COMMAND_ENABLE_ARG:
                if (arguments.length < 3 || arguments[2] == null) {
                    return String.format(messageService.getStandardMessageForKey(MESSAGE_FEATURE_WRONG_COMMAND_SYNTAX_DEFAULT), USER_TAG + userName);
                }
                try {
                    botFeatureService.addFeatureToChannel(channelName, FeatureEnum.valueOf(arguments[2].toUpperCase()));
                } catch (final IllegalArgumentException e) {
                    LOG.error("No feature found for code {}", arguments[2], e);
                    return String.format(messageService.getStandardMessageForKey(MESSAGE_FEATURE_WRONG_COMMAND_SYNTAX_DEFAULT), USER_TAG + userName);
                }
                return String.format(messageService.getStandardMessageForCommand(FEATURE), USER_TAG + userName, getActiveFeatureListForChannel(channelName));
            case FEATURE_COMMAND_DISABLE_ARG:
                if (arguments.length < 3 || arguments[2] == null) {
                    return String.format(messageService.getStandardMessageForKey(MESSAGE_FEATURE_WRONG_COMMAND_SYNTAX_DEFAULT), USER_TAG + userName);
                }
                try {
                    botFeatureService.removeFeatureFromChannel(channelName, FeatureEnum.valueOf(arguments[2].toUpperCase()));
                } catch (final IllegalArgumentException e) {
                    LOG.error("No feature found for code {}", arguments[2], e);
                    return String.format(messageService.getStandardMessageForKey(MESSAGE_FEATURE_WRONG_COMMAND_SYNTAX_DEFAULT), USER_TAG + userName);
                }
                return String.format(messageService.getStandardMessageForCommand(FEATURE), USER_TAG + userName, getActiveFeatureListForChannel(channelName));
        }
        return String.format(messageService.getStandardMessageForKey(MESSAGE_FEATURE_WRONG_COMMAND_SYNTAX_DEFAULT), USER_TAG + userName);
    }

    private String getActiveFeatureListForChannel(final String channelName) {
        return botFeatureService.getActiveFeaturesForChannel(channelName).stream()
                .map(FeatureEnum::toString)
                .collect(Collectors.joining(", "));
    }
}
