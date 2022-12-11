package com.chatbot.util.emotes.seventv;

public class SevenTVOwner {
    private String id;
    private String twitch_id;
    private String login;
    private String display_name;
    private SevenTVRole role;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTwitch_id() {
        return twitch_id;
    }

    public void setTwitch_id(String twitch_id) {
        this.twitch_id = twitch_id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public SevenTVRole getRole() {
        return role;
    }

    public void setRole(SevenTVRole role) {
        this.role = role;
    }
}
