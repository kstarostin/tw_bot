package com.chatbot.feature.generator.impl.util;

import org.apache.commons.lang3.StringUtils;

public class ResponseGeneratorUtil {

    public static final String SENTENCE_SHORTENER = "(?<=[.!?])";
    public static final String SPACE_SHORTENER = StringUtils.SPACE;

    private ResponseGeneratorUtil() {
    }

    public static String sanitize(final String message) {
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
        return sanitizedMessage.trim();
    }

    public static String shorten(final String message, final int maxLength, final String delimiterRegex) {
        final String[] parts =  message.split(delimiterRegex);
        if (parts.length > 1) {
            StringBuilder shortenedMessage = new StringBuilder();
            for (final String part : parts) {
                if (shortenedMessage.length() + part.length() < maxLength) {
                    shortenedMessage.append(part).append(StringUtils.SPACE);
                } else if (StringUtils.isNotEmpty(shortenedMessage.toString())) {
                    return shortenedMessage.toString().trim();
                } else {
                    return message;
                }
            }
            return shortenedMessage.toString().trim();
        }
        return message;
    }
}
