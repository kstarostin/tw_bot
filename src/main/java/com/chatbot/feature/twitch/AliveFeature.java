package com.chatbot.feature.twitch;

import com.chatbot.feature.generator.GeneratorRequest;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;
import com.chatbot.feature.generator.impl.OpenAIResponseGenerator;
import com.chatbot.service.PeriodCacheService;
import com.chatbot.service.ModerationService;
import com.chatbot.service.RandomizerService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultPeriodCacheServiceImpl;
import com.chatbot.service.impl.DefaultModerationServiceImpl;
import com.chatbot.service.impl.DefaultRandomizerServiceImpl;
import com.chatbot.util.FeatureEnum;
import com.chatbot.util.emotes.TwitchEmote;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

    private static final Set<String> USER_FRIEND_LIST = Set.of("0mskbird", "yura_atlet", "1skybox1", "chenushka", "hereticjz", "skvdee", "svetloholmov", "prof_133", "kiber_bober",
            "poni_prancing", "greyraise", "panthermania", "tachvnkin", "tesla013", "shinigamidth", "enteris");

    private static final int MIN_CHATTING_RATE = 1;
    private static final int MAX_CHATTING_RATE = 10;

    private static final int REQUEST_MESSAGE_MAX_LENGTH = 100;

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

        if (isGreetingResponse(channelName, userName, message)) {
            greet(event);
        } else if (isEmoteOnlyMessage(channelId, message)) {
            if (isBotSelfTriggered(channelId, channelName)) {
                respondWithEmote(message, event, StringUtils.EMPTY);
            }
        } else if (canBeTriggeredByTag(channelId, channelName, userName)) {
            if (isBotTaggedDirectly(message) && isEmoteOnlyMessage(channelId, messageService.getMessageSanitizer(message).withNoTags().sanitizeForTwitch(channelId, channelName))) {
                respondWithEmote(messageService.getMessageSanitizer(message).withNoTags().sanitizeForTwitch(channelId, channelName), event, userName);
            } else {
                respondWithMessage(message, event);
            }
        }
    }

    private void greet(final ChannelMessageEvent event) {
        final String channelId = event.getChannel().getId();
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder().withText(buildGreetingText(channelId, channelName, userName));

        if (randomizerService.flipCoin(2)) {
            responseMessageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 3, GREETING, POG, HAPPY));
        }
        greetWithDelay(channelId, channelName, userName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), event);
    }

    private void respondWithEmote(final String message, final ChannelMessageEvent event, final String userTag) {
        final String channelId = event.getChannel().getId();
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder();

        final List<TwitchEmote> emoteSet = getEmoteSet(message);
        if (CollectionUtils.isNotEmpty(emoteSet)) {
            responseMessageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 3, emoteSet)).withUserTag(userTag);
        } else {
            splitOnParts(message).stream()
                    .findFirst()
                    .ifPresent(emote -> responseMessageBuilder.withEmotes(List.of(new TwitchEmote(emote))).withUserTag(userTag));
        }
        final int delay = calculateResponseDelayTime(responseMessageBuilder);
        messageService.sendMessageWithDelay(channelName, responseMessageBuilder, calculateResponseDelayTime(responseMessageBuilder), null);
        if (responseMessageBuilder.isNotEmpty()) {
            final Calendar sentAt = Calendar.getInstance();
            sentAt.add(Calendar.MILLISECOND, delay);
            saveBotMessageForChannelId(channelId, new BotMessage(responseMessageBuilder.buildForTwitch(), sentAt));
            cacheService.cacheGreeting(channelName, userName);
        }
    }

    private void respondWithMessage(final String message, final ChannelMessageEvent event) {
        final String channelId = event.getChannel().getId();
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final DefaultMessageServiceImpl.MessageBuilder responseMessageBuilder = messageService.getMessageBuilder();

        List<String> lastMessagesForRequest = null;
        if (isBotTaggedDirectly(message)) {
            lastMessagesForRequest = List.of(message);
        } else if (isNoOneTagged(message) && (isBotTaggedIndirectly(channelName, message) || isBotSelfTriggered(channelId, channelName))) {
            lastMessagesForRequest = getLastMessagesLimited(channelId, userName);
        }
        if (CollectionUtils.isEmpty(lastMessagesForRequest)) {
            return;
        }
        responseMessageBuilder.withText(generateResponseText(channelId, channelName, userName, lastMessagesForRequest));
        if (responseMessageBuilder.isNotEmpty()) {
            if (randomizerService.flipCoin(3)) {
                responseMessageBuilder.withEmotes(twitchEmoteService.buildRandomEmoteList(channelId, 2, CONFUSION, HAPPY));
            }
            sendMessage(channelId, channelName, userName, responseMessageBuilder, event);
            cacheService.cacheGreeting(channelName, userName);
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
        return isBotTagged(message, tags);
    }

    private boolean isBotTaggedIndirectly(final String channelName, final String message) {
        return isBotTagged(message, new ArrayList<>(CollectionUtils.emptyIfNull(configurationService.getConfiguration(channelName).getAdditionalBotTagNames())));
    }

    private boolean isBotTagged(final String message, final List<String> tags) {
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
        final List<String> messageTokens = splitOnParts(message);
        for (final String token : messageTokens) {
            if (!twitchEmoteService.isEmote(channelId, token)) {
                return false;
            }
        }
        return true;
    }

    private List<TwitchEmote> getEmoteSet(final String emoteText) {
        final List<String> emotes = splitOnParts(emoteText);
        for (final String emote : emotes) {
            for (final List<TwitchEmote> set : ALL_SETS) {
                if (set.stream().anyMatch(setEmote -> setEmote.toString().equals(emote))) {
                    return set;
                }
            }
        }
        return Collections.emptyList();
    }

    private List<String> splitOnParts(final String text) {
        return List.of(text.trim().split(StringUtils.SPACE));
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

        LOG.debug(String.format("Current chatting rate [%d], independent response probability [%s], last bot message must be [%d] seconds ago", chattingRate, chattingRate * 10 + "%", secondsToCheck));
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

    private String buildGreetingText(final String channelId, final String channelName, final String userName) {
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
        final String requestMessage = messageService.getMessageSanitizer(messageTemplate)
                .withNoTags()
                .withNoEmotes()
                .withMaxLength(REQUEST_MESSAGE_MAX_LENGTH)
                .withDelimiter(",")
                .sanitizeForTwitch(channelId, channelName);

        final String responseMessage = generateResponseText(GeneratorRequest.getBuilder()
                .withRequestMessage(requestMessage)
                .withChannelId(channelId)
                .withChannelName(channelName)
                .withUserName(configurationService.getConfiguration().getSuperAdmin())
                .withResponseSanitized()
                .withRequestMessageIncluded()
                .withMaxResponseLength(100)
                .buildForTwitch());
        return responseMessage.replaceAll(" +", StringUtils.SPACE).trim();
    }

    private List<String> getLastMessagesLimited(final String channelId, final String userName) {
        List<ChannelMessageEvent> lastMessageEvents = getLastChatMessageEventsForChannelIdAndUserName(channelId, userName).stream()
                .filter(event -> !isEmoteOnlyMessage(channelId, event.getMessage()))
                .collect(Collectors.toList());
        if (lastMessageEvents.size() < 3) {
            lastMessageEvents = getLastChatMessageEventsForChannelId(channelId).stream()
                    .filter(event -> !isEmoteOnlyMessage(channelId, event.getMessage()))
                    .collect(Collectors.toList());
        }
        Collections.reverse(lastMessageEvents);

        final StringBuilder message = new StringBuilder();
        final List<String> lastMessages = new ArrayList<>();

        for (final ChannelMessageEvent event : lastMessageEvents) {
            lastMessages.add(event.getMessage());
            message.append(event.getMessage()).append(StringUtils.SPACE);
            if (message.length() > REQUEST_MESSAGE_MAX_LENGTH) {
                break;
            }
        }
        return lastMessages.stream().limit(3).collect(Collectors.toList());
    }

    private String generateResponseText(final String channelId, final String channelName, final String userName, final List<String> lastMessages) {
        final String requestMessage = messageService.getMessageSanitizer(String.join(StringUtils.SPACE, lastMessages))
                .withNoTags()
                .withNoEmotes()
                .withMaxLength(REQUEST_MESSAGE_MAX_LENGTH)
                .withDelimiter()
                .sanitizeForTwitch(channelId, channelName);

        if (StringUtils.isNotBlank(requestMessage)) {
            return generateResponseText(GeneratorRequest.getBuilder()
                    .withRequestMessage(requestMessage)
                    .withChannelId(channelId)
                    .withChannelName(channelName)
                    .withUserName(userName)
                    .withResponseSanitized()
                    .withMaxResponseLength(200)
                    .buildForTwitch());
        }
        return StringUtils.EMPTY;
    }

    private String generateResponseText(final GeneratorRequest request) {
        String response = openAIresponseGenerator.generate(request);
        if (StringUtils.isBlank(response)) {
            response = balabobaResponseGenerator.generate(request);
        }
        return response;
    }

    private int calculateResponseDelayTime(final DefaultMessageServiceImpl.MessageBuilder messageBuilder) {
        final int minDelayTime = 1;
        final int maxDelayTime = 7;
        final int[] dividerArray = IntStream.range(minDelayTime, maxDelayTime + 1).toArray();
        final int divider;
        final String message = messageBuilder.buildForTwitch();
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
                messageBuilder.withUserTag(userName, startsWithTag);
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
            saveBotMessageForChannelId(channelId, new BotMessage(messageBuilder.buildForTwitch(), sentAt));
            cacheService.cacheGreeting(channelName, userName);
        }
    }

    private void sendMessage(final String channelId, final String channelName, final String userName, final DefaultMessageServiceImpl.MessageBuilder messageBuilder, final ChannelMessageEvent event) {
        switch (getReplyType()) {
            case SEND_RESPONSE:
                final boolean startsWithTag = randomizerService.flipCoin(2);
                messageService.sendMessage(channelName, messageBuilder.withUserTag(userName, startsWithTag), null);
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
