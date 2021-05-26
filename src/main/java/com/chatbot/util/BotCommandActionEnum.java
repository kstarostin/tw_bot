package com.chatbot.util;

public enum BotCommandActionEnum {
    SIMPLE_RESPONSE ("simple_response");

    private final String text;

    BotCommandActionEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
