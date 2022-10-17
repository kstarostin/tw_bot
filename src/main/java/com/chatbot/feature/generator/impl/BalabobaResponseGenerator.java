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
import java.util.Set;
import java.util.stream.Stream;

public class BalabobaResponseGenerator implements ResponseGenerator {
    private static BalabobaResponseGenerator instance;

    private final Logger LOG = LoggerFactory.getLogger(BalabobaResponseGenerator.class);

    private final static int GENERATED_MESSAGE_MAX_LENGTH = 250;
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

            final String request = "{\"query\":\"" + message + "\",\"intro\":0,\"filter\":" + getStyle() + "}";
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
            return e.toString();
        }
    }

    private String getStyle() {
        //return Stream.of(BalabobaStyle.values()).skip((int) (Set.of(BalabobaStyle.values()).size() * Math.random())).findFirst().orElse(BalabobaStyle.NO_STYLE).toString();
        return BalabobaStyle.SHORT_STORIES.toString();
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
