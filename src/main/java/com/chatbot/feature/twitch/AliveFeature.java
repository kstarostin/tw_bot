package com.chatbot.feature.twitch;

import com.chatbot.feature.generator.GeneratorRequest;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.impl.OpenAIResponseGenerator;
import com.chatbot.feature.generator.impl.util.ResponseGeneratorUtil;
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

import static com.chatbot.util.emotes.TwitchEmote.Sets.*;

public class AliveFeature extends AbstractFeature {
    private final Logger LOG = LoggerFactory.getLogger(AliveFeature.class);

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

    // Chat caches
    private static final Map<String, Queue<ChannelMessageEvent>> CHAT_MESSAGE_HISTORY_MAP = new HashMap<>();
    private static final Map<String, Queue<BotMessage>> BOT_MESSAGE_HISTORY_MAP = new HashMap<>();

    private final ResponseGenerator openAIresponseGenerator = OpenAIResponseGenerator.getInstance();
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
        saveChatMessageEventForChannelId(channelId, event);

        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder();
        if (isGreetingResponse(channelName, userName, message)) {
            responseMessageBuilder.withText(buildGreetingTextNew(channelId, channelName, userName));
            if (randomizerService.flipCoin(2)) {
                responseMessageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteLine(channelId, 3, GREETING, POG, HAPPY));
            }
            greetWithDelay(channelId, channelName, userName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), event);
        } else if (isEmoteOnlyMessage(channelId, message)) {
            final List<String> emoteSet = getEmoteSet(message);
            if (CollectionUtils.isNotEmpty(emoteSet) && isBotSelfTriggered(channelId, channelName)) {
                responseMessageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteLine(channelId, 3, emoteSet));
                final int delay = calculateResponseDelayTime(responseMessageBuilder);
                messageService.sendMessageWithDelay(channelName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), null);
                if (responseMessageBuilder.isNotEmpty()) {
                    final Calendar sentAt = Calendar.getInstance();
                    sentAt.add(Calendar.MILLISECOND, delay);
                    saveBotMessageForChannelId(channelId, new BotMessage(responseMessageBuilder.toString(), sentAt));
                    cacheService.cacheGreeting(channelName, userName);
                }
            }
        } else if (canBeTriggeredByTag(channelId, channelName, userName)) {
            if (isBotTaggedDirectly(message)) {
                responseMessageBuilder.withText(generateResponseText(channelId, channelName, userName, List.of(message)));
                if (responseMessageBuilder.isNotEmpty()) {
                    if (randomizerService.flipCoin(3)) {
                        responseMessageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteLine(channelId, 2, CONFUSION, HAPPY));
                    }
                    sendMessage(channelId, channelName, userName, responseMessageBuilder, event);
                    cacheService.cacheGreeting(channelName, userName);
                }
            } else if (isNoOneTagged(message) && (isBotTaggedIndirectly(channelName, message) || isBotSelfTriggered(channelId, channelName))) {
                responseMessageBuilder.withText(generateResponseText(channelId, channelName, userName, getLastMessages(channelId, userName)));
                if (responseMessageBuilder.isNotEmpty()) {
                    if (randomizerService.flipCoin(3)) {
                        responseMessageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteLine(channelId, 2, CONFUSION, HAPPY));
                    }
                    sendMessage(channelId, channelName, userName, responseMessageBuilder, event);
                    cacheService.cacheGreeting(channelName, userName);
                }
            }
        }
    }

    private boolean isGreetingResponse(final String channelName, final String userName, final String message) {
        return isGreetingEnabled(channelName)
                && !isUserGreeted(channelName, userName)
                && (isUserInFriendList(userName) || isRandomGreeting())
                && !(isBotTaggedDirectly(message) || isBotTaggedIndirectly(channelName, message));
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

    private boolean isBotTaggedDirectly(final String message) {
        final List<String> tags = List.of(TAG_CHARACTER + configurationService.getTwitchBotName(), configurationService.getTwitchBotName());
        return isTagged(message, tags);
    }

    private boolean isBotTaggedIndirectly(final String channelName, final String message) {
        return isTagged(message, new ArrayList<>(CollectionUtils.emptyIfNull(configurationService.getConfiguration(channelName).getAdditionalBotTagNames())));
    }

    private boolean isTagged(final String message, final List<String> tags) {
        for (final String tag : tags) {
            if (message.equalsIgnoreCase(tag)
                    || message.toLowerCase().startsWith(tag.toLowerCase() + StringUtils.SPACE)
                    || message.toLowerCase().startsWith(tag.toLowerCase() + ",")
                    || message.toLowerCase().contains(StringUtils.SPACE + tag.toLowerCase() + StringUtils.SPACE)
                    || message.toLowerCase().endsWith(StringUtils.SPACE + tag.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean canBeTriggeredByTag(final String channelId, final String channelName, final String userName) {
        final int secondsToCheck = configurationService.getConfiguration(channelName).getTagTriggerMaxWaitTime();
        return isSuperAdmin(userName) || !hasMessagesSentInChannelSince(channelId, secondsToCheck);
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

    private boolean isBotSelfTriggered(final String channelId, final String channelName) {
        final int chattingRate = calculateCurrentChattingRate(channelId);
        final int subtract = chattingRate - MIN_CHATTING_RATE;
        final int maxSecondsToCheck = configurationService.getConfiguration(channelName).getSelfTriggerMaxWaitTime();
        final int rateMultiplier = maxSecondsToCheck / (MAX_CHATTING_RATE * 2);
        int secondsToCheck = maxSecondsToCheck - chattingRate * rateMultiplier;

        LOG.info(String.format("Current chatting rate [%d], independent response probability [%s], last bot message must be [%d] seconds ago",
                chattingRate, chattingRate * 10 + "%", secondsToCheck));
        if (hasMessagesSentInChannelSince(channelId, secondsToCheck)) {
            return false;
        }
        return randomizerService.rollDice(MAX_CHATTING_RATE - subtract) == 0;
    }

    private boolean hasMessagesSentInChannelSince(final String channelId, final int lastSeconds) {
        final Calendar timePoint = Calendar.getInstance();
        timePoint.add(Calendar.SECOND, -lastSeconds);
        return getLastBotMessagesForChannelId(channelId).stream().anyMatch(botMessage -> botMessage.getPostedAt().after(timePoint));
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

    private String buildGreetingTextNew(final String channelId, final String channelName, final String userName) {
        final List<String> greetingTokens = new ArrayList<>(List.of(GREETING_TOKEN));
        if (randomizerService.flipCoin(2)) {
            greetingTokens.add(USERNAME_TOKEN);
            Collections.shuffle(greetingTokens);
        }

        final StringBuilder sb = new StringBuilder();
        greetingTokens.forEach(token -> sb.append(StringUtils.SPACE).append(token));

        String messageTemplate = sb.toString();
        for (final String token : greetingTokens) {
            final String replacement = messageService.getPersonalizedMessageForKey("message.greeting." + token + "." + userName.toLowerCase(), "message.greeting." + token + ".default");
            messageTemplate = messageTemplate.replace(token, replacement);
        }

        final String requesterId = "tw:" + channelId + ":" + configurationService.getConfiguration().getSuperAdmin();
        String requestMessage = sanitizeRequestMessage(channelId, channelName, messageTemplate.replace(ADDITION_TOKEN, StringUtils.EMPTY));
        if (requestMessage.endsWith(".")) {
            requestMessage = StringUtils.replaceOnce(requestMessage, ".", ",");
        }

        final String responseMessage = generate(new GeneratorRequest(requestMessage, requesterId, true, 100, true));
        return responseMessage.replaceAll(" +", StringUtils.SPACE).trim();
    }

    private List<String> getLastMessages(final String channelId, final String userName) {
        List<ChannelMessageEvent> lastMessages = getLastChatMessageEventsForChannelIdAndUserName(channelId, userName).stream()
                .filter(event -> !isEmoteOnlyMessage(channelId, event.getMessage()))
                .collect(Collectors.toList());
        if (lastMessages.size() < 3) {
            lastMessages = getLastChatMessageEventsForChannelId(channelId).stream()
                    .filter(event -> !isEmoteOnlyMessage(channelId, event.getMessage()))
                    .collect(Collectors.toList());
        }
        Collections.reverse(lastMessages);
        return lastMessages.stream().limit(3).map(AbstractChannelMessageEvent::getMessage).collect(Collectors.toList());
    }

    private String generateResponseText(final String channelId, final String channelName, final String userName, final List<String> lastMessages) {
        final String requesterId = "tw:" + channelId + ":" + userName;
        final String requestMessage = ResponseGeneratorUtil.shorten(
                sanitizeRequestMessage(channelId, channelName, String.join(StringUtils.SPACE, lastMessages)), 100, ResponseGeneratorUtil.SPACE_SHORTENER);

        return StringUtils.isNotBlank(requestMessage)
                ? generate(new GeneratorRequest(requestMessage, requesterId, true, 150, false))
                : StringUtils.EMPTY;
    }

    private String generate(final GeneratorRequest request) {
        String response = openAIresponseGenerator.generate(request);
        if (StringUtils.isBlank(response)) {
            response = balabobaResponseGenerator.generate(request);
        }
        return StringUtils.isNotEmpty(response) ? ResponseGeneratorUtil.moderate(response) : response;
    }

    private String sanitizeRequestMessage(final String channelId, final String channelName, final String message) {
        final int maxLength = 100;
        final String[] delimiters = {".", "?", "!"};

        final String[] words =  message.trim().split(StringUtils.SPACE);
        final List<String> sanitizedWords = Arrays.stream(words)
                .filter(word -> !word.startsWith(TAG_CHARACTER))
                .filter(word -> !StringUtils.equalsIgnoreCase(word, configurationService.getTwitchBotName()))
                .filter(word -> !StringUtils.equalsAnyIgnoreCase(word, CollectionUtils.emptyIfNull(configurationService.getConfiguration(channelName).getAdditionalBotTagNames()).toArray(new String[0])))
                .filter(word -> !twitchEmoteService.isEmote(channelId, word))
                .collect(Collectors.toList());
        if (sanitizedWords.isEmpty()) {
            return StringUtils.EMPTY;
        }
        String sanitizedMessage = String.join(StringUtils.SPACE, sanitizedWords);
        final StringBuilder sanitizedMessageBuilder = new StringBuilder();
        for (final String word : sanitizedWords) {
            if (sanitizedMessageBuilder.length() + word.length() + 1 < maxLength) {
                sanitizedMessageBuilder.append(word).append(StringUtils.SPACE);
            } else if (StringUtils.isNotEmpty(sanitizedMessageBuilder.toString())) {
                sanitizedMessage = sanitizedMessageBuilder.toString();
                break;
            } else {
                sanitizedMessage = (sanitizedMessage.length() > maxLength ? sanitizedMessage.substring(0, maxLength) : sanitizedMessage).trim();
                return StringUtils.endsWithAny(sanitizedMessage, delimiters) ? sanitizedMessage : sanitizedMessage + ".";
            }
        }
        sanitizedMessage = sanitizedMessage.trim();
        return StringUtils.endsWithAny(sanitizedMessage, delimiters) ? sanitizedMessage : sanitizedMessage + ".";
    }

    private int calculateResponseDelayTime(final DefaultMessageServiceImpl.MessageBuilder messageBuilder) {
        final int minDelayTime = 1;
        final int maxDelayTime = 7;
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

    private void greetWithDelay(final String channelId, final String channelName, final String userName, final DefaultMessageServiceImpl.MessageBuilder messageBuilder, final int delay, final ChannelMessageEvent event) {
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
        if (messageBuilder.isNotEmpty()) {
            final Calendar sentAt = Calendar.getInstance();
            sentAt.add(Calendar.MILLISECOND, delay);
            saveBotMessageForChannelId(channelId, new BotMessage(messageBuilder.toString(), sentAt));
            cacheService.cacheGreeting(channelName, userName);
        }
    }

    private void sendMessage(final String channelId, final String channelName, final String userName, final DefaultMessageServiceImpl.MessageBuilder messageBuilder, final ChannelMessageEvent event) {
        switch (getReplyType()) {
            case SEND_RESPONSE:
                final boolean startsWithTag = randomizerService.flipCoin(2);
                final boolean isNoTagCharIncluded = !randomizerService.flipCoin(4);
                final String tag = isNoTagCharIncluded ? userName : TAG_CHARACTER + userName;
                messageBuilder.withUserTag(tag, startsWithTag);
                messageService.sendMessage(channelName, messageBuilder, null);
                break;
            case SEND_REPLY:
                messageService.sendMessage(channelName, messageBuilder, event);
                break;
            case SEND_MESSAGE:
                messageService.sendMessage(channelName, messageBuilder, null);
                break;
        }
        if (messageBuilder.isNotEmpty()) {
            saveBotMessageForChannelId(channelId, new BotMessage(messageBuilder.toString()));
        }
    }

    private MessageType getReplyType() {
        //return RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP[randomizerService.rollDice(RESPONSE_MESSAGE_TYPE_PROBABILITY_MAP.length)];
        return MessageType.SEND_RESPONSE; // todo fix response to reply chain
    }

    private int calculateCurrentChattingRate(final String channelId) {
        final Queue<ChannelMessageEvent> lastMessageEvents = getLastChatMessageEventsForChannelId(channelId);
        if (CollectionUtils.isEmpty(lastMessageEvents)) {
            return MIN_CHATTING_RATE;
        }
        final Calendar lastMinute = Calendar.getInstance();
        lastMinute.add(Calendar.MINUTE, -1);
        final int rate =  Math.min((int) lastMessageEvents.stream().filter(messageEvent -> messageEvent.getFiredAt().after(lastMinute)).count(), MAX_CHATTING_RATE);
        LOG.debug("Calculated chatting rate [" + rate + "]");
        return rate;
    }

    private void saveChatMessageEventForChannelId(final String channelId, final ChannelMessageEvent messageEvent) {
        final Queue<ChannelMessageEvent> lastMessageEvents = getLastChatMessageEventsForChannelId(channelId);
        lastMessageEvents.add(messageEvent);
        CHAT_MESSAGE_HISTORY_MAP.put(channelId, lastMessageEvents);
    }

    private Queue<ChannelMessageEvent> getLastChatMessageEventsForChannelIdAndUserName(final String channelId, final String userName) {
        return getLastChatMessageEventsForChannelId(channelId).stream()
                .filter(event -> userName.equals(event.getUser().getName()))
                .collect(Collectors.toCollection(() -> new CircularFifoQueue<>(10)));
    }

    private Queue<ChannelMessageEvent> getLastChatMessageEventsForChannelId(final String channelId) {
        return CHAT_MESSAGE_HISTORY_MAP.containsKey(channelId) ? CHAT_MESSAGE_HISTORY_MAP.get(channelId) : new CircularFifoQueue<>(10);
    }

    private void saveBotMessageForChannelId(final String channelId, final BotMessage botMessage) {
        final Queue<BotMessage> lastMessages = getLastBotMessagesForChannelId(channelId);
        lastMessages.add(botMessage);
        BOT_MESSAGE_HISTORY_MAP.put(channelId, lastMessages);
    }

    private Queue<BotMessage> getLastBotMessagesForChannelId(final String channelId) {
        return BOT_MESSAGE_HISTORY_MAP.containsKey(channelId) ? BOT_MESSAGE_HISTORY_MAP.get(channelId) : new CircularFifoQueue<>(10);
    }

    private static class BotMessage {
        private String message;
        private Calendar postedAt;

        public BotMessage(final String message) {
            this(message, Calendar.getInstance());
        }

        public BotMessage(final String message, Calendar postedAt) {
            this.message = message;
            this.postedAt = postedAt;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Calendar getPostedAt() {
            return postedAt;
        }

        public void setPostedAt(Calendar postedAt) {
            this.postedAt = postedAt;
        }
    }

    private enum MessageType {
        SEND_RESPONSE,
        SEND_REPLY,
        SEND_MESSAGE
    }
}
