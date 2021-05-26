package com.chatbot.util;

public enum BotCommandEnum {
    SIMPLE_RESPONSE ("simple_response");

    private final String text;

    BotCommandEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
