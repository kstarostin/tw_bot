package com.chatbot.service.impl;

import com.chatbot.service.GlobalConfigurationService;
import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.service.MessageService;
import com.chatbot.util.TechnicalBotCommandTriggerEnum;
import com.chatbot.util.EmoteEnum;
import com.chatbot.util.MessageUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


public class DefaultMessageServiceImpl implements MessageService {
    private static DefaultMessageServiceImpl instance;

    private Properties messageProperties;

    private static final long SECONDS_BETWEEN_MESSAGE_REPEAT = 60;

    // last messages of chat per channel
    // Map<channel, messageList<Map<message, time>>>
    private final Map<String, CircularFifoQueue<Map<String, Date>>> trackedChannelMessages = new HashMap<>();
    // last messages of bot per channel
    // Map<channel, messageList<Map<message, time>>>
    private final Map<String, CircularFifoQueue<Map<String, Date>>> trackedBotMessages = new HashMap<>();

    private final GlobalConfigurationService globalConfigurationService = DefaultGlobalConfigurationServiceImpl.getInstance();

    private DefaultMessageServiceImpl() {
    }

    public static synchronized DefaultMessageServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultMessageServiceImpl();
        }
        return instance;
    }

    @Override
    public void respond(final AbstractChannelEvent event, final String responseMessage, final String userName) {
        if (responseMessage.isEmpty()) {
            return;
        }

        final List<String> lastMinuteMessages = getTrackedBotMessagesForChannel(event.getChannel().getName()).stream()
                .filter(messageEntry -> {
                    final String message = messageEntry.keySet().iterator().next();
                    final Date messageDate = messageEntry.get(message);
                    final Date minuteAgo = Date.from(LocalDateTime.now().minusSeconds(SECONDS_BETWEEN_MESSAGE_REPEAT).atZone(ZoneId.systemDefault()).toInstant());
                    return messageDate.after(minuteAgo);
                }).map(messageEntry -> messageEntry.keySet().iterator().next())
                .collect(Collectors.toList());
        if (lastMinuteMessages.contains(responseMessage) && !userName.equals(globalConfigurationService.getSuperAdminName())) {
            return;
        }

        System.out.printf("Response message [%s]", responseMessage);
        trackBotMessageForChannel(event.getChannel().getName(), responseMessage);
        event.getTwitchChat().sendMessage(event.getChannel().getName(), responseMessage);
    }

    @Override
    public boolean isBotQuoted(final ChannelMessageEvent event) {
        return MessageUtils.isBotQuoted(event.getMessage());
    }

    @Override
    public boolean containsTrackedRepeatedEmote(final ChannelMessageEvent event) {
        return extractEmotePart(event) != null;
    }

    @Override
    public void trackUserMessageForChannel(final String channelName, final String message) {
        final Map<String, Date> newMessageEntry = new HashMap<>();
        newMessageEntry.put(message, new Date());
        getTrackedUserMessagesForChannel(channelName).add(newMessageEntry);
    }

    @Override
    public CircularFifoQueue<Map<String, Date>> getTrackedUserMessagesForChannel(final String channelName) {
        CircularFifoQueue<Map<String, Date>> trackedMessages = trackedChannelMessages.get(channelName);
        if (trackedMessages == null) {
            trackedMessages = new CircularFifoQueue<>(6);
            trackedChannelMessages.put(channelName, trackedMessages);
        }
        return trackedMessages;
    }

    @Override
    public EmoteEnum extractEmotePart(final ChannelMessageEvent event) {
        return Arrays.stream(EmoteEnum.values())
                .filter(emote -> {
                    final long count = getTrackedUserMessagesForChannel(event.getChannel().getName()).stream()
                            .filter(messageEntry -> {
                                final String message = messageEntry.keySet().iterator().next();
                                final Date messageDate = messageEntry.get(message);
                                final Date minuteAgo = Date.from(LocalDateTime.now().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
                                return messageDate.after(minuteAgo)
                                        && (message.equals(emote.toString())
                                        || message.startsWith(emote + " ")
                                        || message.endsWith(" " + emote)
                                        || message.contains(" " + emote + " "));
                            })
                            .count();
                    return count >= 3;
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getStandardMessageForCommand(final TechnicalBotCommandTriggerEnum commandEnum) {
        //return fixCyrillicEncoding(getMessageProperties().getProperty("command." + commandEnum.toString() + ".default"));
        return getMessageProperties().getProperty("command." + commandEnum.toString() + ".default");
    }

    @Override
    public String getStandardMessageForKey(final String key) {
        //return fixCyrillicEncoding(getMessageProperties().getProperty(key));
        return getMessageProperties().getProperty(key);
    }

    private void trackBotMessageForChannel(final String channelName, final String message) {
        final Map<String, Date> newMessageEntry = new HashMap<>();
        newMessageEntry.put(message, new Date());
        getTrackedBotMessagesForChannel(channelName).add(newMessageEntry);
    }

    private CircularFifoQueue<Map<String, Date>> getTrackedBotMessagesForChannel(final String channelName) {
        CircularFifoQueue<Map<String, Date>> trackedMessages = trackedBotMessages.get(channelName);
        if (trackedMessages == null) {
            trackedMessages = new CircularFifoQueue<>(100);
            trackedBotMessages.put(channelName, trackedMessages);
        }
        return trackedMessages;
    }

    private Properties getMessageProperties() {
        if (messageProperties == null) {
            try {
                messageProperties = new Properties();
                messageProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("messages/messages.properties"));
            } catch (final Exception e) {
                e.printStackTrace();
                System.out.println("Unable to load default messages. Exiting application.");
                System.exit(1);
            }
        }
        return messageProperties;
    }
}
