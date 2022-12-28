package com.chatbot.feature.generator.impl;

import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.util.ResponseGeneratorUtil;
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
    private final static int GENERATED_MESSAGE_MAX_NUMBER_OF_ATTEMPTS = 5;

    private BalabobaResponseGenerator () {
    }

    public static synchronized BalabobaResponseGenerator getInstance() {
        if (instance == null) {
            instance = new BalabobaResponseGenerator();
        }
        return instance;
    }

    @Override
    public String generate(final String requesterId, final String requestMessage, final boolean shortenResponse, final boolean sanitizeResponse, final boolean includeRequest) {
        return generate(requesterId, requestMessage, shortenResponse, sanitizeResponse, includeRequest, null);
    }

    @Override
    public String generate(final String requesterId, final String requestMessage, final boolean shortenResponse, final boolean sanitizeResponse, final boolean includeRequest, final Style style) {
        Style requestStyle = style != null ? style : getRandomStyle();
        final String payload = createPayload(requestMessage, 0, requestStyle.toString(), 0);

        String generatedMessage;
        if (shortenResponse) {
            int maxLength = calculateRandomLength();
            int generateCounter = 1;
            do {
                generatedMessage = ResponseGeneratorUtil.shorten(generateByBalaboba(payload, generateCounter), maxLength, ResponseGeneratorUtil.SENTENCE_SHORTENER);
                generateCounter++;
            } while ((generatedMessage.length() == 0 || generatedMessage.length() > maxLength) && generateCounter <= GENERATED_MESSAGE_MAX_NUMBER_OF_ATTEMPTS);
        } else {
            generatedMessage = generateByBalaboba(payload, 1);
        }
        if (sanitizeResponse) {
            generatedMessage = ResponseGeneratorUtil.sanitize(generatedMessage);
        }
        return includeRequest ? requestMessage + StringUtils.SPACE + generatedMessage : generatedMessage;
    }

    private int calculateRandomLength() {
        int random = new Random().nextInt(GENERATED_MESSAGE_MAX_LENGTHS.length);
        return GENERATED_MESSAGE_MAX_LENGTHS[random];
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
