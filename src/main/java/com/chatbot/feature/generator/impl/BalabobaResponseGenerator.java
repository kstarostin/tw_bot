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
import java.util.Set;
import java.util.stream.Stream;

public class BalabobaResponseGenerator implements ResponseGenerator {
    private static BalabobaResponseGenerator instance;

    private final Logger LOG = LoggerFactory.getLogger(BalabobaResponseGenerator.class);

    private final static int[] GENERATED_MESSAGE_MAX_LENGTHS = {50, 100, 150, 200, 250, 300, 350};
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
    public String generate(final String message) {
        String generatedMessage;
        int maxLength = calculateRandomLength();
        int generateCounter = 1;
        do {
            generatedMessage = shorten(generateWithBalaboba(message), maxLength);
            generateCounter++;
        } while (generatedMessage.length() > maxLength && generateCounter <= GENERATED_MESSAGE_MAX_NUMBER_OF_ATTEMPTS);
        return sanitizeMessage(generatedMessage);
    }

    private int calculateRandomLength() {
        int random = new Random().nextInt(GENERATED_MESSAGE_MAX_LENGTHS.length);
        return GENERATED_MESSAGE_MAX_LENGTHS[random];
    }

    private String sanitizeMessage(final String message) {
        String sanitizedMessage = message.trim();
        if (sanitizedMessage.startsWith("-")) {
            sanitizedMessage = sanitizedMessage.replaceFirst("-", StringUtils.EMPTY);
        }
        if (sanitizedMessage.contains("\n")) {
            sanitizedMessage = sanitizedMessage.replaceAll("\n", StringUtils.SPACE);
        }
        return sanitizedMessage;
    }

    private String generateWithBalaboba(final String message) {
        try {
            final URLConnection http = new URL("https://zeapi.yandex.net/lab/api/yalm/text3").openConnection();
            http.setRequestProperty("Content-Type", "application/json");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();

            final String request = "{\"query\":\"" + message + "\",\"intro\":0,\"style\":" + getStyle() + ",\"filter\":0}";
            LOG.info("Balaboba request: " + request);
            http.getOutputStream().write(request.getBytes(StandardCharsets.UTF_8));

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

    private String getStyle() {
        return Stream.of(BalabobaStyle.values()).skip((int) (Set.of(BalabobaStyle.values()).size() * Math.random())).findFirst().orElse(BalabobaStyle.NO_STYLE).toString();
        //return BalabobaStyle.TOSTS.toString();
    }

    private String shorten(final String message, final int maxLength) {
        final String[] sentences =  message.split("[.!?]");
        if (sentences.length > 1) {
            StringBuilder shortenedMessage = new StringBuilder();
            for (final String sentence : sentences) {
                if (shortenedMessage.length() + sentence.length() < maxLength) {
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

    private enum BalabobaStyle {
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

        BalabobaStyle(final String style) {
            this.style = style;
        }

        @Override
        public String toString() {
            return style;
        }
    }
}
