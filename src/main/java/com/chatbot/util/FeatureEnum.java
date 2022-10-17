package com.chatbot.util;

public enum FeatureEnum {
    LOGGING ("logging"),
    SUBSCRIPTION ("subscription"),
    COMMAND ("command"),
    ALIVE("alive");

    private final String text;

    FeatureEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
