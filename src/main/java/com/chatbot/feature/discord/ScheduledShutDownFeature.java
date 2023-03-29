package com.chatbot.feature.discord;

import com.chatbot.feature.generator.GeneratorRequest;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.OpenAIResponseGenerator;
import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.util.emotes.DiscordEmote;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ScheduledShutDownFeature implements DiscordFeature {
    private static ScheduledShutDownFeature instance;

    private final Logger LOG = LoggerFactory.getLogger(ScheduledShutDownFeature.class);

    private static final LocalDateTime AUTO_SHUT_DOWN_DATE_TIME = LocalDateTime.of(2023, Month.MARCH, 31, 21, 59, 0);
    private static final String FAREWELL_MESSAGE_PROMPT = "Спокойной ночи.";

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    private final ResponseGenerator openAIresponseGenerator = OpenAIResponseGenerator.getInstance();

    private ScheduledShutDownFeature() {
    }

    public static synchronized ScheduledShutDownFeature getInstance() {
        if (instance == null) {
            instance = new ScheduledShutDownFeature();
        }
        return instance;
    }

    public void handle(final GatewayDiscordClient gateway) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime scheduledDateTime = AUTO_SHUT_DOWN_DATE_TIME;
        LOG.warn("Auto shut down is set to [{}], message will be sent to channel [{}]", scheduledDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), getChannelId());

        if (scheduledDateTime.isAfter(now)) {
            final Date twoSecondsLaterAsDate = Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant());
            new Timer().schedule(new ScheduledMessageTask(gateway), twoSecondsLaterAsDate);
        }
    }

    private class ScheduledMessageTask extends TimerTask {
        final GatewayDiscordClient gateway;

        ScheduledMessageTask(final GatewayDiscordClient gateway) {
            this.gateway = gateway;
        }

        @Override
        public void run() {
            final String responseText = generate(buildRequestMessage());
            final String response = messageService.getMessageBuilder()
                    .withText(responseText)
                    .withEmotes(List.of(DiscordEmote.KebirowHomeGuild.FeelsOkayMan))
                    .buildForDiscord();
            gateway.rest().getChannelById(Snowflake.of(getChannelId())).createMessage(response).subscribe();
            shutDownWithDelay(1);
        }
    }

    private GeneratorRequest buildRequestMessage() {
        return GeneratorRequest.getBuilder()
                .withRequestMessage(FAREWELL_MESSAGE_PROMPT)
                .withRequestMessageIncluded()
                .withChannelId(getChannelId())
                .withUserName(StringUtils.EMPTY + "#" + StringUtils.EMPTY)
                .withResponseSanitized()
                .withMaxResponseLength(250).buildForDiscord();
    }

    private String generate(final GeneratorRequest request) {
        return openAIresponseGenerator.generate(request);
    }

    private void shutDownWithDelay(final int delayMinutes) {
        try {
            TimeUnit.MINUTES.sleep(delayMinutes);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(-1);
    }

    private String getChannelId() {
        return RED_ROOM_TEXT;
    }
}
