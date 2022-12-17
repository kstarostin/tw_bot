package com.chatbot.service.impl;

import com.chatbot.service.RandomizerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DefaultRandomizerServiceImpl implements RandomizerService {
    private static DefaultRandomizerServiceImpl instance;

    private static final int DEFAULT_EXPONENT_VALUE = 1;

    private DefaultRandomizerServiceImpl() {
    }

    public static synchronized DefaultRandomizerServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultRandomizerServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean flipCoin() {
        return rollDice(2) == 0;
    }

    @Override
    public boolean flipCoin(final int exponent) {
        return rollDiceExponentially(2, exponent) == 0;
    }

    @Override
    public int rollDice(final int sidesNumber) {
        return rollDiceExponentially(sidesNumber, DEFAULT_EXPONENT_VALUE);
    }

    @Override
    public int rollDiceExponentially(final int sidesNumber, final int exponent) {
        final List<Integer> results = new ArrayList<>();
        for (int i = 0, j = sidesNumber; i < sidesNumber; i++, j--) {
            final int numberOfCopies = exponent > DEFAULT_EXPONENT_VALUE ? (int) Math.pow(j, exponent) : DEFAULT_EXPONENT_VALUE;
            results.addAll(Collections.nCopies(numberOfCopies, i));
        }
        return results.get(new Random().nextInt(results.size()));
    }
}
