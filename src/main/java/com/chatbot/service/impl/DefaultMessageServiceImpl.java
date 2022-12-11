package com.chatbot.service.impl;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.util.FeatureEnum;
import com.chatbot.service.MessageService;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;


public class DefaultMessageServiceImpl implements MessageService {
    private static DefaultMessageServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultMessageServiceImpl.class);

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private static final int MAX_MESSAGE_LENGTH = 450;

    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
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
    public void sendMessage(final String channelName, final String responseMessage, final ChannelMessageEvent event) {
        sendMessage(channelName, responseMessage, true, event);
    }

    @Override
    public void sendMessage(final String channelName, final String responseMessage, final boolean isMuteChecked, final ChannelMessageEvent event) {
        if (responseMessage.isEmpty()) {
            return;
        }
        if (!configurationService.getBotName().equalsIgnoreCase(channelName) && !configurationService.getConfiguration().getTwitchChannels().contains(channelName)) {
            return;
        }
        if (isMuteChecked && configurationService.getConfiguration(channelName).isMuted()) {
            return;
        }
        if (botFeatureService.isTwitchFeatureActive(channelName, FeatureEnum.LOGGING)) {
            final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            LOG.info("Channel[{}]-[{}]:[{}]:[{}]", channelName, formatter.format(new Date()) , configurationService.getBotName(), responseMessage);
        }
        final List<String> messageParts = new ArrayList<>();

        if (responseMessage.length() > MAX_MESSAGE_LENGTH) {
            messageParts.addAll(splitByMaxLength(responseMessage));
        } else {
            messageParts.add(responseMessage);
        }

        messageParts.forEach(part -> {
            if (event != null) {
                event.reply(twitchClientService.getTwitchClient().getChat(), part);
            } else {
                twitchClientService.getTwitchClient().getChat().sendMessage(channelName, part);
            }
        });
    }

    @Override
    public void sendMessageWithDelay(final String channelName, final String responseMessage, final int delay, final ChannelMessageEvent event) {
        if (configurationService.getConfiguration(channelName).isMuted()) {
            return;
        }
        if (responseMessage.isEmpty()) {
            return;
        }
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        sendMessage(channelName, responseMessage, event);
                    }
                },
                delay
        );
    }

    @Override
    public String getStandardMessageForKey(final String key) {
        final String propertyMessage = configurationService.getProperties("messages/messages.properties").getProperty(key);
        final String[] messages = StringUtils.isNotEmpty(propertyMessage) ? propertyMessage.split("\\|") : new String[0];
        return messages.length > 0 ? messages[new Random().nextInt(messages.length)] : StringUtils.EMPTY;
    }

    @Override
    public String getPersonalizedMessageForKey(final String personalizedKey, final String defaultKey) {
        final String message = getStandardMessageForKey(personalizedKey);
        if (StringUtils.isEmpty(message)) {
            return getStandardMessageForKey(defaultKey);
        }
        return message;
    }

    private List<String> splitByMaxLength(final String message) {
        final String[] words = message.split("\\s+");
        if (words.length > 1) {
            final List<StringBuilder> splitMessages = new ArrayList<>();
            StringBuilder shortenedMessage = new StringBuilder();
            splitMessages.add(shortenedMessage);
            for (final String word : words) {
                if (shortenedMessage.length() + word.length() + 1 <= MAX_MESSAGE_LENGTH) {
                    shortenedMessage.append(word).append(StringUtils.SPACE);
                } else if (StringUtils.isNotEmpty(shortenedMessage.toString())) {
                    shortenedMessage = new StringBuilder();
                    splitMessages.add(shortenedMessage);
                }
            }
            return splitMessages.stream().map(sb -> sb.toString().trim()).collect(Collectors.toList());
        } else {
            return List.of(message);
        }
    }
}
