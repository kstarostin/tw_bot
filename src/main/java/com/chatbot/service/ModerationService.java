package com.chatbot.service;

import com.github.twitch4j.common.enums.CommandPermission;

import java.util.Set;

public interface ModerationService {

    boolean isSuspiciousMessage(String message, Set<CommandPermission> userPermissions);

    int getSuspiciousWordsMatchCount(String message);

    int getSuspiciousWordsMatchCount(String message, Integer matchThreshold);
}
