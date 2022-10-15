package com.chatbot.feature;

import com.chatbot.service.MessageService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class AliveFeature extends AbstractFeature {

    private final Logger LOG = LoggerFactory.getLogger(AliveFeature.class);

    private final static int GENERATED_MESSAGE_MAX_LENGTH = 250;
    private final static int GENERATED_MESSAGE_MAX_NUMBER_OF_ATTEMPTS = 10;

    private final static Set<String> GREETED_USERS = new HashSet<>();

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();
    private final StaticConfigurationService configurationService = DefaultStaticConfigurationServiceImpl.getInstance();

    public AliveFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String userName = event.getUser().getName();
        if (!isFeatureActive(FeatureEnum.ALIVE)) {
            return;
        }
        if (isBotTagged(event.getMessage())) {
            final String responseMessage = generateResponseMessage(event.getMessage());
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.respondWithDelay(event, userName + " " + responseMessage, calculateResponseDelayTime(responseMessage));
            }
        } else if (!isUserGreeted(userName)) {
            final String responseMessage = String.format(messageService.getStandardMessageForKey("message.hello." + userName.toLowerCase()), userName);
            if (StringUtils.isNotEmpty(responseMessage)) {
                messageService.respondWithDelay(event, responseMessage, calculateResponseDelayTime(responseMessage));
                GREETED_USERS.add(userName);
            }
        }
    }

    private boolean isBotTagged(final String message) {
        return StringUtils.containsIgnoreCase(message, configurationService.getBotName());
    }

    private boolean isUserGreeted(final String userName) {
        return GREETED_USERS.contains(userName);
    }

    private String generateResponseMessage(final String message) {
        String generatedMessage;
        int generateCounter = 1;
        do {
            generatedMessage = shorten(generateWithBalaboba(message));
            generateCounter++;
        } while (generatedMessage.length() > GENERATED_MESSAGE_MAX_LENGTH && generateCounter <= GENERATED_MESSAGE_MAX_NUMBER_OF_ATTEMPTS);
        return generatedMessage;
    }

    private String generateWithBalaboba(final String message) {
        try {
            final URLConnection http = new URL("https://zeapi.yandex.net/lab/api/yalm/text3").openConnection();
            http.setRequestProperty("Content-Type", "application/json");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();

            final String rq = "{\"query\":\"" + message + "\",\"intro\":0,\"filter\":1}";
            http.getOutputStream().write(rq.getBytes(StandardCharsets.UTF_8));

            final BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            final String line = reader.readLine();
            reader.close();

            final JSONObject jsonObject = new JSONObject(line);
            return jsonObject.getString("text");
        }
        catch (final Exception e) {
            LOG.error(e.getMessage());
            return e.toString();
        }
    }

    private String shorten(final String message) {
        final String[] sentences =  message.split("[.!?]");
        if (sentences.length > 1) {
            StringBuilder shortenedMessage = new StringBuilder();
            for (final String sentence : sentences) {
                if (shortenedMessage.length() + sentence.length() < GENERATED_MESSAGE_MAX_LENGTH) {
                    shortenedMessage.append(sentence).append(".");
                } else if (StringUtils.isNotEmpty(shortenedMessage.toString())) {
                    return shortenedMessage.toString();
                } else {
                    return message;
                }
            }
            return shortenedMessage.toString();
        }
        return message;
    }

    private int calculateResponseDelayTime(final String message) {
        return message.length() / 5 * 1000;
    }
}
