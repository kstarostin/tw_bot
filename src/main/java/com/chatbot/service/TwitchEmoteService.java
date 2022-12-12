package com.chatbot.service;

import com.chatbot.util.emotes.bttv.BTTVEmote;
import com.chatbot.util.emotes.ffz.FFZEmoticon;
import com.chatbot.util.emotes.seventv.SevenTVEmote;

import java.util.List;

public interface TwitchEmoteService {
    List<SevenTVEmote> getGlobal7TVEmotes();
    List<SevenTVEmote> getChannel7TVEmotes(String channelName);
    List<BTTVEmote> getGlobalBTTVEmotes();
    List<BTTVEmote> getChannelBTTVEmotes(String channelId);
    List<FFZEmoticon> getGlobalFFZEmotes();
    List<FFZEmoticon> getChannelFFZEmotes(String channelName);
}
