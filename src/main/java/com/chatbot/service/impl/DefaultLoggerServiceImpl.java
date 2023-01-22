package com.chatbot.service.impl;

import com.chatbot.service.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultLoggerServiceImpl implements LoggerService {
    private static DefaultLoggerServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultLoggerServiceImpl.class);

    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

    private DefaultLoggerServiceImpl() {
    }

    public static synchronized DefaultLoggerServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultLoggerServiceImpl();
        }
        return instance;
    }

    @Override
    public void logTwitchMessage(final String channelName, final String userName, final String message) {
        final String channel = "tw:" + channelName;
        logMessage(channel, userName, message);
    }

    @Override
    public void logDiscordMessage(final String serverName, final String channelName, final String userName, final String message) {
        final String channel = "ds:" + serverName + ":" + channelName;
        logMessage(channel, userName, message);
    }

    @Override
    public void logDiscordReaction(final String serverName, final String channelName, final String userName, final String reaction) {
        final String channel = "ds:" + serverName + ":" + channelName;
        logMessage(channel, userName, "reactions: " + reaction);
    }

    private void logMessage(final String channelName, final String userName, final String message) {
        LOG.info("[{}]-[{}] {}: {}", channelName, formatter.format(new Date()) , userName, message);
    }
}
