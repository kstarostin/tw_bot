package com.chatbot.util;

public enum FeatureEnum {
    LOGGING ("logging"),
    SUBSCRIPTION ("subscription"),
    STREAM ("stream"),
    COMMAND ("command"),
    ALIVE("alive"),
    MODERATOR("moderator");

    private final String text;

    FeatureEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
