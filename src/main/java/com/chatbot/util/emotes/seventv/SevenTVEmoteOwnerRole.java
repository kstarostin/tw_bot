package com.chatbot.util.emotes.seventv;

public class SevenTVEmoteOwnerRole {
    private String id;
    private String name;
    private long position;
    private long color;
    private long allowed;
    private long denied;

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

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public long getColor() {
        return color;
    }

    public void setColor(long color) {
        this.color = color;
    }

    public long getAllowed() {
        return allowed;
    }

    public void setAllowed(long allowed) {
        this.allowed = allowed;
    }

    public long getDenied() {
        return denied;
    }

    public void setDenied(long denied) {
        this.denied = denied;
    }
}
