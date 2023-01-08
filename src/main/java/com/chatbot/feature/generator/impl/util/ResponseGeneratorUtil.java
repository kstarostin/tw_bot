package com.chatbot.feature.generator.impl.util;

import com.chatbot.service.impl.DefaultModerationServiceImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class ResponseGeneratorUtil {
    public static final String SENTENCE_SHORTENER = "(?<=[.!?])";

    private static final String MODERATION_REPLACEMENT_SIGN = "*";

    private static final String BAN_WORDS_PATH = "moderation/ban-words.txt";
    private static final Set<String> BAN_WORDS = new HashSet<>(DefaultModerationServiceImpl.getInstance().readDictionary(BAN_WORDS_PATH));

    private ResponseGeneratorUtil() {
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

    public static String moderate(final String message) {
        String moderatedMessage = message;
        for (String word : BAN_WORDS) {
            if (StringUtils.containsIgnoreCase(moderatedMessage, word)) {
                //final String replacement = MODERATION_REPLACEMENT_SIGN.repeat(word.length());
                final String replacement = word.length() > 2
                        ? word.charAt(0) + MODERATION_REPLACEMENT_SIGN.repeat(word.length() - 2) + word.charAt(word.length() - 1)
                        : MODERATION_REPLACEMENT_SIGN.repeat(word.length());
                moderatedMessage = StringUtils.replace(moderatedMessage, word, replacement);
            }
        }
        return moderatedMessage;
    }
}
