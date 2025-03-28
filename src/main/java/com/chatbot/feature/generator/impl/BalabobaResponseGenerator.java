package com.chatbot.feature.generator.impl;

import com.chatbot.feature.generator.GeneratorRequest;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class BalabobaResponseGenerator extends AbstractResponseGenerator implements ResponseGenerator {
    private static BalabobaResponseGenerator instance;

    private final Logger LOG = LoggerFactory.getLogger(BalabobaResponseGenerator.class);

    private static final String BALABOBA_API_URL = "https://zeapi.yandex.net/lab/api/yalm/text3";
    private final static String REQUEST_TEMPLATE = "{\"query\":\"@@@query@@@\",\"intro\":@@@intro@@@,\"style\":@@@style@@@,\"filter\":@@@filter@@@}";

    private final static int GENERATED_MESSAGE_MAX_NUMBER_OF_ATTEMPTS = 5;

    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    private BalabobaResponseGenerator () {
    }

    public static synchronized BalabobaResponseGenerator getInstance() {
        if (instance == null) {
            instance = new BalabobaResponseGenerator();
        }
        return instance;
    }

    @Override
    public String generate(final GeneratorRequest request) {
        Style requestStyle = request.getResponseStyle() != null ? request.getResponseStyle() : getRandomStyle();
        final String payload = createPayload(request.getRequestMessage(), 0, requestStyle.toString(), 0);

        String generatedMessage;
        if (request.getMaxResponseLength() != null) {
            int generateCounter = 1;
            do {
                generatedMessage = shorten(generateByBalaboba(payload, generateCounter), request.getMaxResponseLength(), SENTENCE_SHORTENER);
                generateCounter++;
            } while ((generatedMessage.length() == 0 || generatedMessage.length() > request.getMaxResponseLength()) && generateCounter <= GENERATED_MESSAGE_MAX_NUMBER_OF_ATTEMPTS);
        } else {
            generatedMessage = generateByBalaboba(payload, 1);
        }
        if (request.isResponseSanitized()) {
            generatedMessage = request.isTwitchRequest()
                    ? messageService.getMessageSanitizer(generatedMessage).sanitizeForTwitch(request.getChannelId(), request.getChannelName())
                    : messageService.getMessageSanitizer(generatedMessage).sanitizeForDiscord();
        }
        final String response = request.isRequestMessageIncluded() ? request.getRequestMessage() + StringUtils.SPACE + generatedMessage : generatedMessage;
        return moderate(response);
    }

    private String generateByBalaboba(final String payload, final int counter) {
        BufferedReader reader = null;
        try {
            final URLConnection http = new URL(BALABOBA_API_URL).openConnection();
            http.setRequestProperty("Content-Type", "application/json");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();

            LOG.info(String.format("Balaboba request #%d: %s", counter, payload));
            http.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));

            reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            final String line = reader.readLine();

            final String responseText = new JSONObject(line).getString("text");
            LOG.info("Balaboba response: " + responseText);

            return responseText;
        } catch (final Exception e) {
            LOG.error("Unexpected error: " + e.getMessage());
            return StringUtils.EMPTY;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    LOG.error("Cannot close reader: " + e.getMessage());
                }
            }
        }
    }

    private String createPayload(final String query, final Integer intro, final String style, final Integer filter) {
        final String defaultValue = "0";
        String requestQuery = REQUEST_TEMPLATE.replace("@@@query@@@", query);
        requestQuery = requestQuery.replace("@@@intro@@@", StringUtils.isNotEmpty(style) ? style : defaultValue);
        requestQuery = requestQuery.replace("@@@style@@@", defaultValue);
        requestQuery = requestQuery.replace("@@@filter@@@", StringUtils.isNotEmpty(style) ? filter.toString() : defaultValue);
        return requestQuery;
    }

    private Style getRandomStyle() {
        //return Stream.of(BalabobaStyle.values()).skip((int) (Set.of(BalabobaStyle.values()).size() * Math.random())).findFirst().orElse(BalabobaStyle.NO_STYLE).toString();
        return Style.NO_STYLE;
    }

    public enum Style {
        NO_STYLE ("0"),
        CONSPIRACY_THEORIES ("1"),
        TV_REPORTS ("2"),
        TOSTS ("3"),
        GUY_QUOTES ("4"),
        ADVERTISING_SLOGANS ("5"),
        SHORT_STORIES ("6"),
        INSTA_CAPTIONS ("7"),
        IN_BRIEF_WIKIPEDIA ("8"),
        MOVIE_SYNOPSES ("9"),
        HOROSCOPE ("10"),
        FOLK_WISDOM ("11"),
        MODERN_ART ("12");

        private final String style;

        Style(final String style) {
            this.style = style;
        }

        @Override
        public String toString() {
            return style;
        }
    }
}
