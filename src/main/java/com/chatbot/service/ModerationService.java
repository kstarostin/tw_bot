package com.chatbot.service;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;

import java.util.Set;

public interface ModerationService {

    boolean isBotModeratorOnChannel(String channelName);

    void banUser(String channelName, String userName, String reason);

    void timeoutUser(String channelName, String userName, String reason, Integer duration);

    boolean isSuspiciousMessage(String channelName, String message, Set<CommandPermission> userPermissions);

    int getSuspiciousWordsMatchCount(String message);

    int getSuspiciousWordsMatchCount(String message, Integer matchThreshold);

    boolean isFirstMessage(ChannelMessageEvent event);

    boolean isFollowing(String userId, String channelId);

    long getFollowAgeInSeconds(String userId, String channelId);

    long getUserAgeInSeconds(String userId);

    Set<String> readDictionary(String path);
}
