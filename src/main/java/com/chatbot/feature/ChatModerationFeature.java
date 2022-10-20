package com.chatbot.feature;

import com.chatbot.service.MessageService;
import com.chatbot.service.ModerationService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultModerationServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.StringUtils;

public class ChatModerationFeature extends AbstractFeature {
    private final ModerationService moderationService = DefaultModerationServiceImpl.getInstance();
    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    public ChatModerationFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        if (!isFeatureActive(FeatureEnum.MODERATOR) || (isActiveOnLiveStreamOnly() && !isStreamLive(event.getChannel().getName()))) {
            return;
        }
        final String userName = event.getUser().getName();
        final String message = event.getMessage();
        if (moderationService.isSuspiciousMessage(message, event.getPermissions())) {

            int violationPoints = calculateViolationPoints(message);


            final String responseMessage = String.format(messageService.getStandardMessageForKey("message.moderation.suspicious"), "@" + userName + " (" + violationPoints + ")");
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.respond(event, responseMessage);
            }
        }
    }

    private int calculateViolationPoints(final String message) {
        int violationPoints = moderationService.getSuspiciousWordsMatchCount(message);
        // todo check if first message
        // todo check user age
        // todo check following
        return violationPoints;
    }
}
