package com.chatbot.feature.twitch;

import com.chatbot.service.ModerationService;
import com.chatbot.service.impl.DefaultModerationServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatModerationFeature extends AbstractFeature {

    private final Logger LOG = LoggerFactory.getLogger(ChatModerationFeature.class);

    private final ModerationService moderationService = DefaultModerationServiceImpl.getInstance();

    public ChatModerationFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String channelName = event.getChannel().getName();
        if (!isFeatureActive(channelName, FeatureEnum.MODERATOR) || (isActiveOnLiveStreamOnly(channelName) && !isStreamLive(event.getChannel().getName())) || !moderationService.isBotModeratorOnChannel(event.getChannel().getName())) {
            return;
        }
        final String userName = event.getUser().getName();
        final String message = event.getMessage();
        if (moderationService.isSuspiciousMessage(channelName, message, event.getPermissions())) {
            final String responseMessage;
            int violationPoints = calculateViolationPoints(message, event);

            if (violationPoints >= getViolationPointsThresholdToBan(channelName)) {
                final String banReasonMessage = messageService.getStandardMessageForKey("message.moderation.ban.reason");
                moderationService.banUser(channelName, event.getUser().getName(), banReasonMessage);

                responseMessage = String.format(messageService.getStandardMessageForKey("message.moderation.ban"), TAG_CHARACTER + userName);
            } else if (violationPoints >= getViolationPointsThresholdToTimeout(channelName)) {
                final String muteReasonMessage = messageService.getStandardMessageForKey("message.moderation.timeout.reason");
                moderationService.timeoutUser(channelName, event.getUser().getName(), muteReasonMessage, getAutoTimeoutTimeSeconds(channelName));

                responseMessage = String.format(messageService.getStandardMessageForKey("message.moderation.timeout"), TAG_CHARACTER + userName);
            } else {
                responseMessage = String.format(messageService.getStandardMessageForKey("message.moderation.suspicious"), TAG_CHARACTER + userName);
            }
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.sendMessage(channelName, responseMessage);
            }
        }
    }

    private int calculateViolationPoints(final String message, final ChannelMessageEvent event) {
        int totalViolationPoints = 0;
        int suspiciousWordsVP = Math.min(moderationService.getSuspiciousWordsMatchCount(message), 5);
        int firstMessageVP = 0;
        if (moderationService.isFirstMessage(event)) {
            firstMessageVP = getViolationPointsForFirstMessage(event.getChannel().getName());
        }
        final long followAgeSeconds = moderationService.getFollowAgeInSeconds(event.getUser().getId(), event.getChannel().getId());
        int followAgeVP = calculateFollowAgeViolationPoints(followAgeSeconds);

        final long userAgeSeconds = moderationService.getUserAgeInSeconds(event.getUser().getId());
        int userAgeVP = calculateUserAgeViolationPoints(userAgeSeconds);

        totalViolationPoints += suspiciousWordsVP + firstMessageVP + followAgeVP + userAgeVP;

        LOG.info(String.format("Total violation points: %s [suspicious words: %s, first message: %s, follow age: %s, user age: %s]", totalViolationPoints, suspiciousWordsVP, firstMessageVP, followAgeVP, userAgeVP));
        return totalViolationPoints;
    }

    // todo make configurable
    private int calculateFollowAgeViolationPoints(final long followAgeSeconds) {
        if (followAgeSeconds == 0) {
            return 5;
        } else if (followAgeSeconds <= 3600) { // 1 hour
            return 4;
        } else if (followAgeSeconds <= 86400) { // 1 day
            return 3;
        } else if (followAgeSeconds <= 2628000) { // 1 month
            return 2;
        } else if (followAgeSeconds <= 31536000) { // 1 year
            return 1;
        }
        return 0;
    }

    // todo make configurable
    private int calculateUserAgeViolationPoints(final long userAgeSeconds) {
        if (userAgeSeconds <= 3600) { // 1 hour
            return 3;
        } else if (userAgeSeconds <= 2628000) { // 1 month
            return 2;
        } else if (userAgeSeconds <= 31536000) { // 1 year
            return 1;
        }
        return 0;
    }

    private int getViolationPointsThresholdToTimeout(final String channelName) {
        return configurationService.getConfiguration(channelName).getViolationPointsThresholdForTimeout();
    }

    private int getViolationPointsThresholdToBan(final String channelName) {
        return configurationService.getConfiguration(channelName).getViolationPointsThresholdForBan();
    }

    private int getViolationPointsForFirstMessage(final String channelName) {
        return configurationService.getConfiguration(channelName).getViolationPointsForFirstMessage();
    }

    private int getAutoTimeoutTimeSeconds(final String channelName) {
        return configurationService.getConfiguration(channelName).getAutoTimeoutTimeSeconds();
    }
}
