package com.chatbot.util.emotes.bttv;

public class BTTVEmote {
    private String id;
    private String code;
    private String imageType;
    private BTTVUser user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public BTTVUser getUser() {
        return user;
    }

    public void setUser(BTTVUser user) {
        this.user = user;
    }
}
