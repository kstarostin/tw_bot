package com.chatbot.service.impl;

import com.chatbot.service.EmoteService;
import com.chatbot.util.EmoteEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chatbot.util.EmoteEnum.*;

public class DefaultEmoteServiceImpl implements EmoteService {
    private static DefaultEmoteServiceImpl instance;

    private DefaultEmoteServiceImpl() {
    }

    public static synchronized DefaultEmoteServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultEmoteServiceImpl();
        }
        return instance;
    }

    @Override
    public Set<String> getAllEmotesForChannel(final String channelName) {
        // todo replace with real service call
        return Arrays.stream(EmoteEnum.values()).map(EmoteEnum::toString).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getBTTVEmotesForChannel(final String channelName) {
        // todo replace with real service call
        return Stream.of(
                boomerTUNE, pepeJAM, PepeLaugh, GuitarTime, PianoTime, billyReady, PEPSICLE, FeelsRainMan, hoSway,
                monkaX, catJAM, BoneZone, pepeGuitar, StreamerDoesntKnow, MmmHmm, ADIX, pepeTarkov, Terpiloid, Ebaka
        ).map(EmoteEnum::toString).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getFFZEmotesForChannel(final String channelName) {
        // todo replace with real service call
        return Stream.of(
                TwoHead, DICKS, EZY, FeelsSpecialMan, FeelsWeirdMan, FeelsWowMan, gachiBASS, Hmmge, KEKW, KEKWait,
                KKomrade, monkaChrist, monkaHmm, monkaStop, Odobryau, Okayge, OMEGALUL, peepo2Lit, peepoKnife,
                peepoS, peepoShortMad, PepoG, Pogey, roflanEbalo
        ).map(EmoteEnum::toString).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getChannelEmotes(final String channelName) {
        // todo replace with real service call
        return Collections.emptySet();
    }
}
