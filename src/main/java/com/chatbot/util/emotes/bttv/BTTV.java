package com.chatbot.util.emotes.bttv;

public class BTTV {
    private String id;
    //private String[] bits;
    private String avatar;
    private BTTVEmote[] channelEmotes;
    private BTTVEmote[] sharedEmotes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public BTTVEmote[] getChannelEmotes() {
        return channelEmotes;
    }

    public void setChannelEmotes(BTTVEmote[] channelEmotes) {
        this.channelEmotes = channelEmotes;
    }

    public BTTVEmote[] getSharedEmotes() {
        return sharedEmotes;
    }

    public void setSharedEmotes(BTTVEmote[] sharedEmotes) {
        this.sharedEmotes = sharedEmotes;
    }
}
