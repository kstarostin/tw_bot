package com.chatbot.util.emotes.ffz;

import java.util.Map;

public class FFZRoom {
    private long _id;
    private long twitch_id;
    private String youtube_id;
    private boolean is_group;
    private String display_name;
    private long set;
    private String moderator_badge;
    private Map<String, String> vip_badge;
    private Map<String, String> mod_urls;
    private Map<String, String> user_badges;
    private Map<String, String> user_badge_ids;
    private String css;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getTwitch_id() {
        return twitch_id;
    }

    public void setTwitch_id(long twitch_id) {
        this.twitch_id = twitch_id;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public boolean isIs_group() {
        return is_group;
    }

    public void setIs_group(boolean is_group) {
        this.is_group = is_group;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getSet() {
        return set;
    }

    public void setSet(long set) {
        this.set = set;
    }

    public String getModerator_badge() {
        return moderator_badge;
    }

    public void setModerator_badge(String moderator_badge) {
        this.moderator_badge = moderator_badge;
    }

    public Map<String, String> getVip_badge() {
        return vip_badge;
    }

    public void setVip_badge(Map<String, String> vip_badge) {
        this.vip_badge = vip_badge;
    }

    public Map<String, String> getMod_urls() {
        return mod_urls;
    }

    public void setMod_urls(Map<String, String> mod_urls) {
        this.mod_urls = mod_urls;
    }

    public Map<String, String> getUser_badges() {
        return user_badges;
    }

    public void setUser_badges(Map<String, String> user_badges) {
        this.user_badges = user_badges;
    }

    public Map<String, String> getUser_badge_ids() {
        return user_badge_ids;
    }

    public void setUser_badge_ids(Map<String, String> user_badge_ids) {
        this.user_badge_ids = user_badge_ids;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }
}
