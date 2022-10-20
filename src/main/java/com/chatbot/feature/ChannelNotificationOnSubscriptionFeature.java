package com.chatbot.feature;

import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelNotificationOnSubscriptionFeature extends AbstractFeature {
    private final Logger LOG = LoggerFactory.getLogger(ChannelNotificationOnSubscriptionFeature.class);

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    public ChannelNotificationOnSubscriptionFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(SubscriptionEvent.class, this::onSubscription);
    }

    public void onSubscription(final SubscriptionEvent event) {
        LOG.debug("Channel [{}] - Event [{}] - User [{}] - Plan [{}] - Months [{}] - GiftedBy [{}] - Message [{}]",
                event.getChannel(), event.getClass().getName(), event.getUser(), event.getSubscriptionPlan(), event.getMonths(), event.getGiftedBy(), event.getMessage());
        final String userName = event.getUser().getName();
        if (!isFeatureActive(FeatureEnum.SUBSCRIPTION) || (isActiveOnLiveStreamOnly() && !isStreamLive(event.getChannel().getName()))) {
            return;
        }
        messageService.respond(event, String.format(messageService.getStandardMessageForKey("message.subscription.default"), userName));
    }
}
