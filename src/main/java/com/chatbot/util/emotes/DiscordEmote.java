package com.chatbot.util.emotes;

import java.util.Objects;

public class DiscordEmote extends AbstractEmote {
    private static final String EMOTE_TEMPLATE = "<:%s:%d>";

    private final Long id;

    public DiscordEmote(final String code,final Long id) {
        super(code);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format(EMOTE_TEMPLATE, getCode(), getId());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DiscordEmote that = (DiscordEmote) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public interface KebirowHomeGuild {
    }

    public interface RedRoomGuild {
        DiscordEmote Pausey = new DiscordEmote("Pausey", 1035132999798358016L);
        DiscordEmote Pogey = new DiscordEmote("Pogey", 1035133015040462869L);
        DiscordEmote deshovka = new DiscordEmote("deshovka", 950496796948447312L);
        DiscordEmote Kippah = new DiscordEmote("Kippah", 1054071733017133197L);
        DiscordEmote Basedge = new DiscordEmote("Basedge", 993919651685859349L);
        DiscordEmote stalk2Head = new DiscordEmote("stalk2Head", 1056446650345857146L);
        DiscordEmote Sadge = new DiscordEmote("Sadge", 1034813380575371356L);
    }
}
