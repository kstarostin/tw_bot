package com.chatbot.util.emotes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public interface Sets {
        List<DiscordEmote> HAPPY = List.of(KebirowHomeGuild.Okayge, KebirowHomeGuild.Basedge, KebirowHomeGuild.Starege, KebirowHomeGuild.Clueless, KebirowHomeGuild.FeelsOkayMan,
                KebirowHomeGuild.FeelsWowMan, KebirowHomeGuild.YEP);
        List<DiscordEmote> POG = List.of(KebirowHomeGuild.KebirowPog, KebirowHomeGuild.Pogey, KebirowHomeGuild.Pogey, KebirowHomeGuild.PagMan, KebirowHomeGuild.stalkPog);
        List<DiscordEmote> COOL = List.of(KebirowHomeGuild.EZ, KebirowHomeGuild.Basedge, KebirowHomeGuild.XyliSiga);
        List<DiscordEmote> LAUGH = List.of(KebirowHomeGuild.OMEGALUL, KebirowHomeGuild.KEKW);
        List<DiscordEmote> DANCE = Collections.emptyList();
        List<DiscordEmote> SAD = List.of(KebirowHomeGuild.Sadge, KebirowHomeGuild.Sadeg, KebirowHomeGuild.FeelsBadMan, KebirowHomeGuild.KEKWait, KebirowHomeGuild.Despairge, KebirowHomeGuild.XyliSiga);
        List<DiscordEmote> GREETING = List.of(KebirowHomeGuild.KKomrade);
        List<DiscordEmote> CONFUSION = List.of(KebirowHomeGuild.Okayeg, KebirowHomeGuild.KEKWait, KebirowHomeGuild.CheNaxyi, KebirowHomeGuild.HUH, KebirowHomeGuild.Pausey,
                KebirowHomeGuild.FeelsSpecialMan, KebirowHomeGuild.XyliNado);
        List<DiscordEmote> SCARY = List.of(KebirowHomeGuild.monkaW);

        Map<DiscordEmote, DiscordEmote> EMOTE_COMBINATIONS = Collections.emptyMap();
    }

    public interface KebirowHomeGuild {
        // Static
        DiscordEmote Basedge = new DiscordEmote("Basedge", 1059848754313842708L);
        DiscordEmote CheNaxyi = new DiscordEmote("CheNaxyi", 1059848842998190150L);
        DiscordEmote Clueless = new DiscordEmote("Clueless", 1059848798265950338L);
        DiscordEmote Despairge = new DiscordEmote("Despairge", 1059852518441615370L);
        DiscordEmote EZ = new DiscordEmote("EZ", 1059854115863281664L);
        DiscordEmote FeelsBadMan = new DiscordEmote("FeelsBadMan", 1059855254553886791L);
        DiscordEmote FeelsOkayMan = new DiscordEmote("FeelsOkayMan", 1059864339395387402L);
        DiscordEmote FeelsSpecialMan = new DiscordEmote("FeelsSpecialMan", 1059848875688599673L);
        DiscordEmote FeelsWowMan = new DiscordEmote("FeelsWowMan", 1059848799608111264L);
        DiscordEmote HUH = new DiscordEmote("HUH", 1059853671329959946L);
        DiscordEmote KEKW = new DiscordEmote("KEKW", 1059848804972642377L);
        DiscordEmote KEKWait = new DiscordEmote("KEKWait", 1059848807589875783L);
        DiscordEmote KKomrade = new DiscordEmote("KKomrade", 1059848757539241984L);
        DiscordEmote KebirowPog = new DiscordEmote("KebirowPog", 1059850120235077732L);
        DiscordEmote Kippah = new DiscordEmote("Kippah", 1059862799993540618L);
        DiscordEmote OMEGALUL = new DiscordEmote("OMEGALUL", 1059848846957617242L);
        DiscordEmote Okayeg = new DiscordEmote("Okayeg", 1059848758826901534L);
        DiscordEmote Okayge = new DiscordEmote("Okayge", 1059848809292763216L);
        DiscordEmote PagMan = new DiscordEmote("PagMan", 1059848765885911170L);
        DiscordEmote PauseMan = new DiscordEmote("PauseMan", 1059848767026778123L);
        DiscordEmote Pausey = new DiscordEmote("Pausey", 1059848762069102622L);
        DiscordEmote Pogey = new DiscordEmote("Pogey", 1059848763931369482L);
        DiscordEmote Sadeg = new DiscordEmote("Sadeg", 1059848760320081960L);
        DiscordEmote Sadge = new DiscordEmote("Sadge", 1059848810282627103L);
        DiscordEmote Starege = new DiscordEmote("Starege", 1059848755999932496L);
        DiscordEmote XyliNado = new DiscordEmote("XyliNado", 1059848858521317438L);
        DiscordEmote XyliSiga = new DiscordEmote("XyliSiga", 1059848860433924186L);
        DiscordEmote YEP = new DiscordEmote("YEP", 1059852310811004948L);
        DiscordEmote monkaW = new DiscordEmote("monkaW", 1059852311977017475L);
        DiscordEmote stalk2Head = new DiscordEmote("stalk2Head", 1059853916830969916L);
        DiscordEmote stalkPog = new DiscordEmote("stalkPog", 1059853915136479383L);
        // Dynamic
        DiscordEmote Aware = new DiscordEmote("Aware", 1059848803567542322L);
        DiscordEmote BoneZone = new DiscordEmote("BoneZone", 1059848867220312194L);
        DiscordEmote DinkDonk = new DiscordEmote("DinkDonk", 1059854474237181972L);
        DiscordEmote FeelsRainMan = new DiscordEmote("FeelsRainMan", 1059848864435273798L);
        DiscordEmote MmmHmm = new DiscordEmote("MmmHmm", 1059849614959837205L);
        DiscordEmote StreamerDoesntKnow = new DiscordEmote("StreamerDoesntKnow", 1059848869355192350L);
        DiscordEmote TeaTime = new DiscordEmote("TeaTime", 1059849395262197846L);
        DiscordEmote VodkaTime = new DiscordEmote("VodkaTime", 1059848877785747476L);
        DiscordEmote XyliBye = new DiscordEmote("XyliBye", 1059848852934492222L);
        DiscordEmote XyliWave = new DiscordEmote("XyliWave", 1059848845107920916L);
        DiscordEmote borpaSpin = new DiscordEmote("borpaSpin", 1059853524378341478L);
        DiscordEmote catNope = new DiscordEmote("catNope", 1059848814502084638L);
        DiscordEmote catYep = new DiscordEmote("catYep", 1059848812790820984L);
        DiscordEmote peepoChat = new DiscordEmote("peepoChat", 1059854625202778142L);
    }

    public interface RedRoomGuild {
        // Static
        DiscordEmote Pausey = new DiscordEmote("Pausey", 1035132999798358016L);
        DiscordEmote Pogey = new DiscordEmote("Pogey", 1035133015040462869L);
        DiscordEmote deshovka = new DiscordEmote("deshovka", 950496796948447312L);
        DiscordEmote Kippah = new DiscordEmote("Kippah", 1054071733017133197L);
        DiscordEmote Basedge = new DiscordEmote("Basedge", 993919651685859349L);
        DiscordEmote stalk2Head = new DiscordEmote("stalk2Head", 1056446650345857146L);
        DiscordEmote Sadge = new DiscordEmote("Sadge", 1034813380575371356L);
    }
}
