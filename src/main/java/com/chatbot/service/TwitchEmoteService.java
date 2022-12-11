package com.chatbot.service;

import com.chatbot.util.emotes.ffz.FFZEmoticon;
import com.chatbot.util.emotes.seventv.SevenTVEmote;

import java.util.List;

public interface TwitchEmoteService {
    List<SevenTVEmote> getGlobal7TVEmotes();
    List<SevenTVEmote> getChannel7TVEmotes(String channelName);
    List<SevenTVEmote> getGlobalBTTVEmotes();
    List<SevenTVEmote> getChannelBTTVEmotes(String channelName);
    List<FFZEmoticon> getGlobalFFZEmotes();
    List<FFZEmoticon> getChannelFFZEmotes(String channelName);
}
