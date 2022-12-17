package com.chatbot.feature.twitch;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.service.PeriodCacheService;
import com.chatbot.service.ModerationService;
import com.chatbot.service.RandomizerService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultPeriodCacheServiceImpl;
import com.chatbot.service.impl.DefaultModerationServiceImpl;
import com.chatbot.service.impl.DefaultRandomizerServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.chatbot.util.emotes.BotEmote.Sets.*;

public class AliveFeature extends AbstractFeature {
    private final Logger LOG = LoggerFactory.getLogger(AliveFeature.class);

    private static final int RND_TRIGGER_MIN_PROBABILITY = 1;
    private static final int RND_TRIGGER_MAX_PROBABILITY = 100;

    private final static MessageType[] RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP = {
            MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE,
            MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE,
            MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE, MessageType.SEND_RESPONSE,
            MessageType.SEND_REPLY, MessageType.SEND_REPLY, MessageType.SEND_REPLY,
            MessageType.SEND_MESSAGE
    };

    private static final String USERNAME_TOKEN = "${name}";
    private static final String GREETING_TOKEN = "${greeting}";
    private static final String ADDITION_TOKEN = "${addition}";

    private static final Set<String> USER_FRIEND_LIST = Set.of("0mskbird", "yura_atlet", "1skybox1", "chenushka", "hereticjz", "skvdee", "svetloholmov", "prof_133", "kiber_bober",
            "poni_prancing", "greyraise", "panthermania", "tachvnkin", "tesla013");

    private static final int MIN_CHATTING_RATE = 1;
    private static final int MAX_CHATTING_RATE = 10;

    private static final Map<String, Queue<ChannelMessageEvent>> MESSAGE_HISTORY_MAP = new HashMap<>();

    private final ResponseGenerator balabobaResponseGenerator = BalabobaResponseGenerator.getInstance();
    private final ModerationService moderationService = DefaultModerationServiceImpl.getInstance();
    private final PeriodCacheService cacheService = DefaultPeriodCacheServiceImpl.getInstance();
    private final RandomizerService randomizerService = DefaultRandomizerServiceImpl.getInstance();

    public AliveFeature(final SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public void onChannelMessage(final ChannelMessageEvent event) {
        final String channelId = event.getChannel().getId();
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        if (!isFeatureActive(channelName, FeatureEnum.ALIVE) || (isActiveOnLiveStreamOnly(channelName) && !isStreamLive(event.getChannel().getName()))) {
            return;
        }
        final String message = event.getMessage();
        if (isCommand(message) || moderationService.isSuspiciousMessage(channelName, message, event.getPermissions())) {
            return;
        }
        rememberMessageEventForChannelId(channelId, event);

        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder();
        if (isGreetingResponse(channelName, userName, message)) {
            responseMessageBuilder.withText(buildGreetingText(userName));
            if (randomizerService.flipCoin(2)) {
                responseMessageBuilder.withEmotes(twitchEmoteService.buildEmoteLine(channelId, 3, GREETING, POG, HAPPY));
            }
            greetWithDelay(channelName, userName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), event);
        } else if (isEmoteOnlyMessage(channelId, message)) {
            final List<String> emoteSet = getEmoteSet(message);
            if (CollectionUtils.isNotEmpty(emoteSet) && !randomizerService.flipCoin(2)) {
                responseMessageBuilder.withEmotes(twitchEmoteService.buildEmoteLine(channelId, 3, emoteSet));
                messageService.sendMessageWithDelay(channelName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), null);
            }
        } else if (isBotTagged(message) || (isNoOneTagged(message) && isRandomBotTrigger(channelId, channelName))) {
            List<ChannelMessageEvent> lastMessages = new ArrayList<>(getLastMessageEventsForChannelIdAndUserName(channelId, userName));
            if (lastMessages.size() < 3) {
                lastMessages = new ArrayList<>(getLastMessageEventsForChannelId(channelId));
            }
            Collections.reverse(lastMessages);
            lastMessages = lastMessages.stream().limit(3).collect(Collectors.toList());

            final String requestMessage = lastMessages.stream().map(AbstractChannelMessageEvent::getMessage).collect(Collectors.joining(StringUtils.SPACE));

            responseMessageBuilder.withText(balabobaResponseGenerator.generate(sanitizeRequestMessage(requestMessage), true, true, false))
                    .withEmotes(twitchEmoteService.buildEmoteLine(channelId, 2, CONFUSION, HAPPY));
            if (responseMessageBuilder.isNotEmpty()) {
                sendMessageWithDelay(channelName, userName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), event);
            }
        }
    }

    private boolean isGreetingResponse(final String channelName, final String userName, final String message) {
        return isGreetingEnabled(channelName) && !isUserGreeted(channelName, userName) && (isUserInFriendList(userName) || isRandomGreeting()) && !isBotTagged(message);
    }

    private boolean isGreetingEnabled(final String channelName) {
        return configurationService.getConfiguration(channelName).isUserGreetingEnabled();
    }

    private boolean isUserGreeted(final String channelName, final String userName) {
        return cacheService.getCachedGreetings(channelName).isPresent() && cacheService.getCachedGreetings(channelName).get().contains(userName);
    }

    private boolean isUserInFriendList(final String userName) {
        return USER_FRIEND_LIST.contains(userName.toLowerCase());
    }

    private boolean isRandomGreeting() {
        return !randomizerService.flipCoin(3);
    }

    private boolean isBotTagged(final String message) {
        return StringUtils.containsIgnoreCase(message, configurationService.getBotName());
    }

    private boolean isEmoteOnlyMessage(final String channelId, final String message) {
        final String[] messageTokens = message.split(StringUtils.SPACE);
        for (final String token : messageTokens) {
            if (!twitchEmoteService.isEmote(channelId, token)) {
                return false;
            }
        }
        return true;
    }

    private List<String> getEmoteSet(final String emoteText) {
        final String[] emotes = emoteText.trim().split(StringUtils.SPACE);
        for (final String emote : emotes) {
            for (final List<String> set : ALL_SETS) {
                if (set.contains(emote)) {
                    return set;
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean isNoOneTagged(final String message) {
        return !message.contains(TAG_CHARACTER);
    }

    private boolean isRandomBotTrigger(final String channelId, final String channelName) {
        //final SplittableRandom random = new SplittableRandom();
        //return random.nextInt(RND_TRIGGER_MIN_PROBABILITY, RND_TRIGGER_MAX_PROBABILITY + 1) <= configurationService.getConfiguration(channelName).getIndependenceRate();
        final int chattingRate = calculateCurrentChattingRate(channelId);
        final int subtract = chattingRate - MIN_CHATTING_RATE;
        return randomizerService.rollDice(MAX_CHATTING_RATE - subtract) == 0;
    }

    private String buildGreetingText(final String userName) {
        final List<String> greetingTokens = Arrays.asList(USERNAME_TOKEN, GREETING_TOKEN, ADDITION_TOKEN);
        Collections.shuffle(greetingTokens);
        final StringBuilder sb = new StringBuilder();
        greetingTokens.forEach(token -> {
            final int exponent = ADDITION_TOKEN.equals(token) ? 3 : 2;
            if (randomizerService.flipCoin(exponent)) {
                sb.append(StringUtils.SPACE).append(token);
            }
        });
        String messageTemplate = sb.toString();
        for (final String token : greetingTokens) {
            final String replacement = messageService.getPersonalizedMessageForKey("message.greeting." + token + "." + userName.toLowerCase(), "message.greeting." + token + ".default");
            messageTemplate = messageTemplate.replace(token, replacement);
        }
        return messageTemplate.replaceAll(" +", StringUtils.SPACE).trim();
    }

    private String sanitizeRequestMessage(final String message) {
        String sanitizedMessage = message;
        if (sanitizedMessage.contains(TAG_CHARACTER)) {
            final String taggedUserName = StringUtils.substringBefore(StringUtils.substringAfter(sanitizedMessage, TAG_CHARACTER), StringUtils.SPACE);
            sanitizedMessage = sanitizedMessage.replace(TAG_CHARACTER + taggedUserName, StringUtils.EMPTY);
        }
        if (sanitizedMessage.startsWith(",")) {
            sanitizedMessage = StringUtils.removeStart(sanitizedMessage, ",");
        }
        sanitizedMessage = sanitizedMessage.trim();
        return sanitizedMessage;
    }

    private int calculateResponseDelayTime(final DefaultMessageServiceImpl.MessageBuilder messageBuilder) {
        final int minDelayTime = 3;
        final int maxDelayTime = 15;
        final int[] dividerArray = IntStream.range(minDelayTime, maxDelayTime + 1).toArray();
        final int divider;
        final String message = messageBuilder.toString();
        if (message.length() / maxDelayTime == 0) {
            divider = minDelayTime;
        } else if (message.length() / maxDelayTime > dividerArray.length) {
            divider = maxDelayTime;
        } else {
            divider = dividerArray[(message.length() / maxDelayTime) > 0 ? (message.length() / maxDelayTime) - 1 : 0];
        }
        return message.length() * 1000 / divider;
    }

    private void greetWithDelay(final String channelName, final String userName, final DefaultMessageServiceImpl.MessageBuilder messageBuilder, final int delay, final ChannelMessageEvent event) {
        ChannelMessageEvent replyEvent = null;
        switch (getReplyType()) {
            case SEND_RESPONSE:
                final boolean startsWithTag = randomizerService.flipCoin(2);
                messageBuilder.withUserTag(TAG_CHARACTER + userName, startsWithTag);
                break;
            case SEND_REPLY:
                replyEvent = event;
                break;
            case SEND_MESSAGE:
                break;
        }
        messageService.sendMessageWithDelay(channelName, messageBuilder, delay, replyEvent);
        cacheService.cacheGreeting(channelName, userName);
    }

    private void sendMessageWithDelay(final String channelName, final String userName, final DefaultMessageServiceImpl.MessageBuilder messageBuilder, final int delay, final ChannelMessageEvent event) {
        switch (getReplyType()) {
            case SEND_RESPONSE:
                final boolean startsWithTag = randomizerService.flipCoin(2);
                final boolean isNoTagCharIncluded = !randomizerService.flipCoin(4);
                final String tag = isNoTagCharIncluded ? userName : TAG_CHARACTER + userName;
                messageBuilder.withUserTag(tag, startsWithTag);
                messageService.sendMessageWithDelay(channelName, messageBuilder, delay, null);
                break;
            case SEND_REPLY:
                messageService.sendMessageWithDelay(channelName, messageBuilder, delay, event);
                break;
            case SEND_MESSAGE:
                messageService.sendMessageWithDelay(channelName, messageBuilder, delay, null);
                break;
        }
    }

    private MessageType getReplyType() {
        return RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP[randomizerService.rollDice(RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP.length)];
    }

    private enum MessageType {
        SEND_RESPONSE,
        SEND_REPLY,
        SEND_MESSAGE;
    }

    private int calculateCurrentChattingRate(final String channelId) {
        final Queue<ChannelMessageEvent> lastMessageEvents = getLastMessageEventsForChannelId(channelId);
        if (CollectionUtils.isEmpty(lastMessageEvents)) {
            return MIN_CHATTING_RATE;
        }
        final Calendar lastMinute = Calendar.getInstance();
        lastMinute.add(Calendar.MINUTE, -1);
        final int rate =  Math.min((int) lastMessageEvents.stream().filter(messageEvent -> messageEvent.getFiredAt().after(lastMinute)).count(), MAX_CHATTING_RATE);
        LOG.info("Current chatting rate [" + rate + "]");
        return rate;
    }

    private void rememberMessageEventForChannelId(final String channelId, final ChannelMessageEvent messageEvent) {
        final Queue<ChannelMessageEvent> lastMessageEvents = getLastMessageEventsForChannelId(channelId);
        lastMessageEvents.add(messageEvent);
        MESSAGE_HISTORY_MAP.put(channelId, lastMessageEvents);
    }

    private Queue<ChannelMessageEvent> getLastMessageEventsForChannelIdAndUserName(final String channelId, final String userName) {
        return getLastMessageEventsForChannelId(channelId).stream()
                .filter(event -> userName.equals(event.getUser().getName()))
                .collect(Collectors.toCollection(() -> new CircularFifoQueue<>(10)));
    }

    private Queue<ChannelMessageEvent> getLastMessageEventsForChannelId(final String channelId) {
        return MESSAGE_HISTORY_MAP.containsKey(channelId) ? MESSAGE_HISTORY_MAP.get(channelId) : new CircularFifoQueue<>(10);
    }
}
