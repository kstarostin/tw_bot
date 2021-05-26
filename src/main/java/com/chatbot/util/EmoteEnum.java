package com.chatbot.util;

public enum EmoteEnum {
    // BTTV
    boomerTUNE("boomerTUNE"),
    pepeJAM("pepeJAM"),
    PepeLaugh("PepeLaugh"),
    GuitarTime("GuitarTime"),
    PianoTime("PianoTime"),
    billyReady("billyReady"),
    PEPSICLE("PEPSICLE"),
    FeelsRainMan("FeelsRainMan"),
    hoSway("hoSway"),
    monkaX("monkaX"),
    catJAM("catJAM"),
    BoneZone("BoneZone"),
    pepeGuitar("pepeGuitar"),
    StreamerDoesntKnow("StreamerDoesntKnow"),
    MmmHmm("MmmHmm"),
    // adixred channel
    ADIX("ADIX"),
    pepeTarkov("pepeTarkov"),
    Terpiloid("Terpiloid"),
    Ebaka("Ebaka"),

    // FFZ
    TwoHead("2Head"),
    DICKS("DICKS"),
    EZY("EZY"),
    FeelsSpecialMan("FeelsSpecialMan"),
    FeelsWeirdMan("FeelsWeirdMan"),
    FeelsWowMan("FeelsWowMan"),
    gachiBASS("gachiBASS"),
    Hmmge("Hmmge"),
    KEKW("KEKW"),
    KEKWait("KEKWait"),
    KKomrade("KKomrade"),
    monkaChrist("monkaChrist"),
    monkaHmm("monkaHmm"),
    monkaStop("monkaStop"),
    Odobryau("Odobryau"),
    Okayge("Okayge"),
    OMEGALUL("OMEGALUL"),
    peepo2Lit("peepo2Lit"),
    peepoKnife("peepoKnife"),
    peepoS("peepoS"),
    peepoShortMad("peepoShortMad"),
    PepoG("PepoG"),
    Pogey("Pogey"),
    roflanEbalo("roflanEbalo");

    private final String text;

    EmoteEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
