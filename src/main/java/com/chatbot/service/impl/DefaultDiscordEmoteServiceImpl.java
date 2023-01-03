package com.chatbot.service.impl;

import com.chatbot.service.DiscordEmoteService;
import com.chatbot.service.RandomizerService;
import com.chatbot.util.emotes.DiscordEmote;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.chatbot.util.emotes.DiscordEmote.Sets.EMOTE_COMBINATIONS;

public class DefaultDiscordEmoteServiceImpl implements DiscordEmoteService {
    private static DefaultDiscordEmoteServiceImpl instance;

    private final RandomizerService randomizerService = DefaultRandomizerServiceImpl.getInstance();

    private DefaultDiscordEmoteServiceImpl() {
    }

    public static synchronized DefaultDiscordEmoteServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultDiscordEmoteServiceImpl();
        }
        return instance;
    }

    @Override
    public List<DiscordEmote> buildRandomEmoteList(int maxNumberOfEmotes, List<DiscordEmote>... emoteSets) {
        final int numberOfEmotes = randomizerService.rollDiceExponentially(maxNumberOfEmotes, 2) + 1;

        final List<DiscordEmote> selectedEmotes = new ArrayList<>();
        for (int i = 0; i < numberOfEmotes; i++) {
            if (i > 0) {
                final DiscordEmote previousEmote = selectedEmotes.get(i - 1);
                if (EMOTE_COMBINATIONS.containsKey(previousEmote) && randomizerService.flipCoin()) {
                    selectedEmotes.add(EMOTE_COMBINATIONS.get(previousEmote));
                } else if (randomizerService.flipCoin()) {
                    selectedEmotes.add(previousEmote);
                } else {
                    selectedEmotes.add(getRandomEmoteFromSets(emoteSets));
                }
            } else {
                selectedEmotes.add(getRandomEmoteFromSets(emoteSets));
            }
        }
        return selectedEmotes;
    }

    @SafeVarargs
    private DiscordEmote getRandomEmoteFromSets(final List<DiscordEmote>... emoteSets) {
        final int setNumber = randomizerService.rollDiceExponentially(emoteSets.length, 2);
        final List<DiscordEmote> selectedSet = emoteSets[setNumber].parallelStream().collect(Collectors.toList());
        final int index = randomizerService.rollDiceExponentially(selectedSet.size(), 2);
        return selectedSet.get(index);
    }
}
