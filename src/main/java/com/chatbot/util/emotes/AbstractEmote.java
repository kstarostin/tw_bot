package com.chatbot.util.emotes;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

public abstract class AbstractEmote {
    private final String code;
    protected List<? extends AbstractEmote> combinedWith;

    protected AbstractEmote(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    abstract AbstractEmote withCombinations(final List<? extends AbstractEmote> combinations);

    public List<? extends AbstractEmote> getCombinedWith() {
        return combinedWith;
    }

    public boolean isCombination() {
        return CollectionUtils.isNotEmpty(combinedWith);
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractEmote that = (AbstractEmote) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
