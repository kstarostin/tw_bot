package com.chatbot.service;

import com.chatbot.util.emotes.seventv.SevenTVEmote;

import java.util.List;

public interface TwitchEmoteService {
    List<SevenTVEmote> getGlobal7TVEmotes();
    List<SevenTVEmote> getChannel7TVEmotes(String channelName);
}
