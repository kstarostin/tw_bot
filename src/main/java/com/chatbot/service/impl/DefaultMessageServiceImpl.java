package com.chatbot.service.impl;

import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.chatbot.service.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class DefaultMessageServiceImpl implements MessageService {
    private static DefaultMessageServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultMessageServiceImpl.class);

    private DefaultMessageServiceImpl() {
    }

    public static synchronized DefaultMessageServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultMessageServiceImpl();
        }
        return instance;
    }

    @Override
    public void respond(final AbstractChannelEvent event, final String responseMessage) {
        if (responseMessage.isEmpty()) {
            return;
        }
        LOG.info("Response message [{}]", responseMessage);
        event.getTwitchChat().sendMessage(event.getChannel().getName(), responseMessage);
    }

    @Override
    public void respondWithDelay(final AbstractChannelEvent event, final String responseMessage, final int delay) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        respond(event, responseMessage);
                    }
                },
                delay
        );
    }

    @Override
    public String getStandardMessageForKey(final String key) {
        final String propertyMessage = getMessageProperties().getProperty(key);
        final String[] messages = StringUtils.isNotEmpty(propertyMessage) ? propertyMessage.split("\\|") : new String[0];
        return messages.length > 0 ? messages[new Random().nextInt(messages.length)] : StringUtils.EMPTY;
    }

    private Properties getMessageProperties() {
        final String resource = "messages/messages.properties";
        Properties messageProperties;
        try {
            LOG.debug("Load default messages from the resource [{}] ...", resource);
            messageProperties = new Properties();
            messageProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource));
            LOG.debug("Loaded default messages from the resource [{}]", resource);
            return messageProperties;
        } catch (final Exception e) {
            LOG.error("Unable to load default messages from the resource [{}]. Exiting application...", resource, e);
            System.exit(-1);
            return null;
        }
    }
}
