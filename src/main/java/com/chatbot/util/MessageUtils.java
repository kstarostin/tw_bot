package com.chatbot.util;

import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.impl.DefaultStaticConfigurationServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.chatbot.util.CommandUtils.COMMAND_SIGN;

public class MessageUtils {
    public static final String USER_TAG = "@";

    private static final StaticConfigurationService STATIC_CONFIGURATION_SERVICE = DefaultStaticConfigurationServiceImpl.getInstance();

    private MessageUtils() {
    }

    public static boolean isBotQuoted(final String message) {
        final String botNameQuotedLowerCase = USER_TAG + STATIC_CONFIGURATION_SERVICE.getStaticConfiguration().getBot().get("name").toLowerCase();
        return message.equalsIgnoreCase(botNameQuotedLowerCase)
                || message.toLowerCase().startsWith(botNameQuotedLowerCase + " ")
                || message.toLowerCase().endsWith(" " + botNameQuotedLowerCase)
                || message.toLowerCase().contains(" " + botNameQuotedLowerCase + " ");
    }

    public static String extractQuotingPart(final String message) {
        final String botNameQuotedLowerCase = USER_TAG + STATIC_CONFIGURATION_SERVICE.getStaticConfiguration().getBot().get("name").toLowerCase();
        return message.substring(message.toLowerCase().indexOf(botNameQuotedLowerCase),
                message.toLowerCase().indexOf(botNameQuotedLowerCase) + botNameQuotedLowerCase.length());
    }

    public static TechnicalBotCommandTriggerEnum extractCommandPart(final String message) {
        return Arrays.stream(TechnicalBotCommandTriggerEnum.values())
                .filter(command -> message.equalsIgnoreCase(COMMAND_SIGN + command.toString()) || message.toLowerCase().startsWith(COMMAND_SIGN + command + " "))
                .findFirst()
                .orElse(null);
    }

    /**
     * Temporal fix for cyrillic encoding bug
     */
    public static String fixCyrillicEncoding(final String string) {
        return new String(string.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
