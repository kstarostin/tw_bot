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
    public void sendMessage(String channelName, String responseMessage) {
        sendMessage(channelName, responseMessage, true);
    }

    @Override
    public void sendMessage(final String channelName, final String responseMessage, final boolean isMuteChecked) {
        if (responseMessage.isEmpty()) {
            return;
        }
        if (isMuteChecked && botFeatureService.isBotMuted()) {
            return;
        }
        if (botFeatureService.isTwitchFeatureActive(FeatureEnum.LOGGING)) {
            final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            LOG.info("Channel[{}]-[{}]:[{}]:[{}]", channelName, formatter.format(new Date()) , staticConfigurationService.getBotName(), responseMessage);
        }
        twitchClientService.getTwitchClient().getChat().sendMessage(channelName, responseMessage);
    }

    @Override
    public void sendMessageWithDelay(final String channelName, final String responseMessage, final int delay) {
        if (botFeatureService.isBotMuted()) {
            return;
        }
        if (responseMessage.isEmpty()) {
            return;
        }
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
        final String propertyMessage = staticConfigurationService.getProperties("messages/messages.properties").getProperty(key);
        final String[] messages = StringUtils.isNotEmpty(propertyMessage) ? propertyMessage.split("\\|") : new String[0];
        return messages.length > 0 ? messages[new Random().nextInt(messages.length)] : StringUtils.EMPTY;
    }
}
