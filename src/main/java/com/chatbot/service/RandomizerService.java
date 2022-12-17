package com.chatbot.service;

public interface RandomizerService {
    boolean flipCoin();
    boolean flipCoin(int exponent);
    int rollDice(int sidesNumber);
    int rollDiceExponentially(int sidesNumber, int exponent);
}
