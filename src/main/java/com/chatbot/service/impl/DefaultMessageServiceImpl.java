package com.chatbot.service.impl;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.util.FeatureEnum;
import com.chatbot.service.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class DefaultMessageServiceImpl implements MessageService {
    private static DefaultMessageServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultMessageServiceImpl.class);

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    private final StaticConfigurationService staticConfigurationService = DefaultStaticConfigurationServiceImpl.getInstance();
    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();

    private DefaultMessageServiceImpl() {
    }

    public static synchronized DefaultMessageServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultMessageServiceImpl();
        }
        return instance;
    }

    @Override
    public void sendMessage(final String channelName, final String message) {
        if (message.isEmpty()) {
            return;
        }
        if (botFeatureService.isFeatureActive(FeatureEnum.LOGGING)) {
            final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            LOG.info("Channel[{}]-[{}]:[{}]:[{}]", channelName, formatter.format(new Date()) , staticConfigurationService.getBotName(), message);
        }
        twitchClientService.getTwitchClient().getChat().sendMessage(channelName, message);
    }

    @Override
    public void sendMessageWithDelay(final String channelName, final String responseMessage, final int delay) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        sendMessage(channelName, responseMessage);
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
