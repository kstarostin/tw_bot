package com.chatbot.util.emotes.ffz;

public class FFZSet {
    private long id;
    private long _type;
    private String icon;
    private String title;
    private String css;
    private FFZEmoticon[] emoticons;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long get_type() {
        return _type;
    }

    public void set_type(long _type) {
        this._type = _type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public FFZEmoticon[] getEmoticons() {
        return emoticons;
    }

    public void setEmoticons(FFZEmoticon[] emoticons) {
        this.emoticons = emoticons;
    }
}
