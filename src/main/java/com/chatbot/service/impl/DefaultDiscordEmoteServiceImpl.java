package com.chatbot.service.impl;

import com.chatbot.service.DiscordEmoteService;
import com.chatbot.util.emotes.DiscordEmote;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultDiscordEmoteServiceImpl extends AbstractEmoteServiceImpl<DiscordEmote> implements DiscordEmoteService {
    private static DefaultDiscordEmoteServiceImpl instance;

    private static final String DISCORD_EMOTE_FORMAT = "<.+:\\d{17,19}>";

    private DefaultDiscordEmoteServiceImpl() {
    }

    public static synchronized DefaultDiscordEmoteServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultDiscordEmoteServiceImpl();
        }
        return instance;
    }

    @Override
    public List<DiscordEmote> buildRandomEmoteList(final String channelId, final int maxNumberOfEmotes, List<DiscordEmote>... emoteSets) {
        final int numberOfEmotes = randomizerService.rollDiceExponentially(maxNumberOfEmotes, 2) + 1;

        final List<DiscordEmote> selectedEmotes = new ArrayList<>();
        for (int i = 0; i < numberOfEmotes; i++) {
            final DiscordEmote emote = getRandomEmoteFromSets(channelId, emoteSets);
            if (!emote.isCombination()) {
                selectedEmotes.add(emote);
            } else {
                selectedEmotes.add(new DiscordEmote(emote.getCode(), emote.getId(), emote.isAnimated())
                        .withCombinations(emote.getCombinedWith().stream().filter(combination -> isEmote(channelId, combination.toString())).collect(Collectors.toList())));
            }
        }
        return selectedEmotes;
    }

    @Override
    public boolean isEmote(final String text) {
        return isEmote(null, text);
    }

    @Override
    protected boolean isEmote(final String channelId, final String text) {
        return StringUtils.isNotEmpty(text) && text.matches(DISCORD_EMOTE_FORMAT);
    }
}
