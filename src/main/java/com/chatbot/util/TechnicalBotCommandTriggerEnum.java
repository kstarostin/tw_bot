package com.chatbot.util;

public enum TechnicalBotCommandTriggerEnum {
    COMMANDS ("commands"),
    FEATURE("feature");

    private final String text;

    TechnicalBotCommandTriggerEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
