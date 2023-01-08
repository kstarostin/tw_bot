package com.chatbot.service.impl;

import com.chatbot.service.RandomizerService;
import com.chatbot.util.emotes.AbstractEmote;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractEmoteServiceImpl<T extends AbstractEmote> {
    protected final RandomizerService randomizerService = DefaultRandomizerServiceImpl.getInstance();

    protected T getRandomEmoteFromSets(final String channelId, final List<T>... emoteSets) {
        final int setNumber = randomizerService.rollDiceExponentially(emoteSets.length, 2);
        final List<T> selectedSet = emoteSets[setNumber].parallelStream().filter(emote -> isEmote(channelId, emote.toString())).collect(Collectors.toList());
        final int index = randomizerService.rollDiceExponentially(selectedSet.size(), 2);
        return selectedSet.get(index);
    }

    protected abstract boolean isEmote(final String channelId, final String text);
}
