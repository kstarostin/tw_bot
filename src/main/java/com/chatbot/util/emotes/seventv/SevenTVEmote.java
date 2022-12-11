package com.chatbot.util.emotes.seventv;

public class SevenTVEmote {
    private String id;
    private String name;
    private SevenTVOwner owner;
    private long visibility;
    //private String[] visibility_simple;
    private String mime;
    private long status;
    private String[] tags;
    private long[] width;
    private long[] height;
    //private String[] urls;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SevenTVOwner getOwner() {
        return owner;
    }

    public void setOwner(SevenTVOwner owner) {
        this.owner = owner;
    }

    public long getVisibility() {
        return visibility;
    }

    public void setVisibility(long visibility) {
        this.visibility = visibility;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public long[] getWidth() {
        return width;
    }

    public void setWidth(long[] width) {
        this.width = width;
    }

    public long[] getHeight() {
        return height;
    }

    public void setHeight(long[] height) {
        this.height = height;
    }
}
