package com.chatbot.util.emotes.ffz;

import java.util.Map;

public class FFZGlobal extends FFZRootObject {
    private long[] default_sets;
    private Map<String, String[]> users;

    public long[] getDefault_sets() {
        return default_sets;
    }

    public void setDefault_sets(long[] default_sets) {
        this.default_sets = default_sets;
    }

    public Map<String, String[]> getUsers() {
        return users;
    }

    public void setUsers(Map<String, String[]> users) {
        this.users = users;
    }
}
