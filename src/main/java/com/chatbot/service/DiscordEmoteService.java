package com.chatbot.service;

import com.chatbot.util.emotes.DiscordEmote;

import java.util.List;

public interface DiscordEmoteService {
    List<DiscordEmote> buildRandomEmoteList(String channelId, int maxNumberOfEmotes, List<DiscordEmote>... emoteSets);
}
