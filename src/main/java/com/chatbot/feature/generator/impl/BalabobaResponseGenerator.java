package com.chatbot.feature.generator.impl;

import com.chatbot.feature.generator.ResponseGenerator;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class BalabobaResponseGenerator implements ResponseGenerator {
    private static BalabobaResponseGenerator instance;

    private final Logger LOG = LoggerFactory.getLogger(BalabobaResponseGenerator.class);

    private static final String BALABOBA_API_URL = "https://zeapi.yandex.net/lab/api/yalm/text3";
    private final static String REQUEST_TEMPLATE = "{\"query\":\"@@@query@@@\",\"intro\":@@@intro@@@,\"style\":@@@style@@@,\"filter\":@@@filter@@@}";

    private final static int[] GENERATED_MESSAGE_MAX_LENGTHS = {
            50, 50, 50, 50, 50, 50, 50,
            100, 100, 100, 100, 100, 100,
            150, 150, 150, 150, 150,
            200, 200, 200, 200,
            250, 250, 250,
            300, 300,
            350
    };
    private final static int GENERATED_MESSAGE_MAX_NUMBER_OF_ATTEMPTS = 10;

    private BalabobaResponseGenerator () {
    }

    public static synchronized BalabobaResponseGenerator getInstance() {
        if (instance == null) {
            instance = new BalabobaResponseGenerator();
        }
        return instance;
    }

    @Override
    public String generate(final String requestMessage, final boolean shortenResponse, final boolean sanitizeResponse, final boolean includeRequest) {
        return generate(requestMessage, shortenResponse, sanitizeResponse, includeRequest, null);
    }

    @Override
    public String generate(final String requestMessage, final boolean shortenResponse, final boolean sanitizeResponse, final boolean includeRequest, final Style style) {
        Style requestStyle = style != null ? style : getRandomStyle();
        final String payload = createPayload(requestMessage + StringUtils.SPACE, 0, requestStyle.toString(), 0);

        String generatedMessage;
        if (shortenResponse) {
            int maxLength = calculateRandomLength();
            int generateCounter = 1;
            do {
                generatedMessage = shorten(generateByBalaboba(payload), maxLength);
                generateCounter++;
            } while (generatedMessage.length() > maxLength && generateCounter <= GENERATED_MESSAGE_MAX_NUMBER_OF_ATTEMPTS);
        } else {
            generatedMessage = generateByBalaboba(payload);
        }
        if (sanitizeResponse) {
            generatedMessage = sanitizeResponseMessage(generatedMessage);
        }
        return includeRequest ? requestMessage + StringUtils.SPACE + generatedMessage : generatedMessage;
    }

    private int calculateRandomLength() {
        int random = new Random().nextInt(GENERATED_MESSAGE_MAX_LENGTHS.length);
        return GENERATED_MESSAGE_MAX_LENGTHS[random];
    }

    private String sanitizeResponseMessage(final String message) {
        String sanitizedMessage = message.trim();
        while (sanitizedMessage.startsWith("-") || sanitizedMessage.startsWith("—") || sanitizedMessage.startsWith("\"")) {
            sanitizedMessage = StringUtils.removeStart(sanitizedMessage, "-");
            sanitizedMessage = StringUtils.removeStart(sanitizedMessage, "—");
            sanitizedMessage = StringUtils.removeStart(sanitizedMessage, "\"");
            sanitizedMessage = sanitizedMessage.trim();
        }
        if (sanitizedMessage.endsWith("\"")) {
            sanitizedMessage = StringUtils.removeEnd(sanitizedMessage, "\"");
            sanitizedMessage = sanitizedMessage.trim();
        }
        if (sanitizedMessage.contains("\n")) {
            sanitizedMessage = sanitizedMessage.replaceAll("\n", StringUtils.SPACE);
        }
        return sanitizedMessage;
    }

    private String generateByBalaboba(final String payload) {
        try {
            final URLConnection http = new URL(BALABOBA_API_URL).openConnection();
            http.setRequestProperty("Content-Type", "application/json");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();

            LOG.info("Balaboba request: " + payload);
            http.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));

            final BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            final String line = reader.readLine();
            reader.close();

            final JSONObject jsonObject = new JSONObject(line);
            LOG.info("Balaboba response: " + jsonObject.getString("text"));
            return jsonObject.getString("text");
        }
        catch (final Exception e) {
            LOG.error(e.getMessage());
            return StringUtils.EMPTY;
        }
    }

    private String createPayload(final String query, final Integer intro, final String style, final Integer filter) {
        final String defaultValue = "0";
        String requestQuery = REQUEST_TEMPLATE.replace("@@@query@@@", query);
        requestQuery = requestQuery.replace("@@@intro@@@", StringUtils.isNotEmpty(style) ? style : defaultValue);
        requestQuery = requestQuery.replace("@@@style@@@", defaultValue);
        requestQuery = requestQuery.replace("@@@filter@@@", StringUtils.isNotEmpty(style) ? "1" : defaultValue);
        return requestQuery;
    }

    private Style getRandomStyle() {
        //return Stream.of(BalabobaStyle.values()).skip((int) (Set.of(BalabobaStyle.values()).size() * Math.random())).findFirst().orElse(BalabobaStyle.NO_STYLE).toString();
        return Style.NO_STYLE;
    }

    private String shorten(final String message, final int maxLength) {
        final String[] sentences =  message.split("(?<=[.!?])");
        if (sentences.length > 1) {
            StringBuilder shortenedMessage = new StringBuilder();
            for (final String sentence : sentences) {
                if (shortenedMessage.length() + sentence.length() < maxLength) {
                    shortenedMessage.append(sentence);
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
        MODERN_ART ("12"),;

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
