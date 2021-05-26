package com.chatbot.util;

import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

public class CommandUtils {
    private static final long SECONDS_A_DAY = 86400;
    private static final long SECONDS_AN_HOUR = 3600;
    private static final long SECONDS_A_MINUTE = 60;
    private static final long HOURS_A_DAY = 24;

    public static final String COMMAND_SIGN = "!";

    private static final String MESSAGE_TIME_DAYS_DEFAULT = "message.time.days.default";
    private static final String MESSAGE_TIME_HOURS_DEFAULT = "message.time.hours.default";
    private static final String MESSAGE_TIME_MINUTES_DEFAULT = "message.time.minutes.default";

    private static final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    private CommandUtils() {
    }

    public static String getCurrentStreamUpTime(final Instant startedAtInstant) {
        final Instant nowInstant = Instant.now();
        long seconds = nowInstant.getEpochSecond() - startedAtInstant.getEpochSecond();
        final long days = seconds > SECONDS_A_DAY ? (seconds / SECONDS_A_DAY) : 0;
        final long hours = seconds > SECONDS_AN_HOUR ? (seconds / SECONDS_AN_HOUR % HOURS_A_DAY) : 0;
        final long minutes = seconds > SECONDS_A_MINUTE ? (seconds / SECONDS_A_MINUTE % SECONDS_A_MINUTE) : 0;

        return (days > 0 ? days + " " + messageService.getStandardMessageForKey(MESSAGE_TIME_DAYS_DEFAULT) + " " : StringUtils.EMPTY) +
                (hours > 0 ? hours + " " + messageService.getStandardMessageForKey(MESSAGE_TIME_HOURS_DEFAULT) + " " : StringUtils.EMPTY) +
                minutes + " " + messageService.getStandardMessageForKey(MESSAGE_TIME_MINUTES_DEFAULT);
    }
}
