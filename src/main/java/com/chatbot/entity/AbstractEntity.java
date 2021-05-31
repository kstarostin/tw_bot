package com.chatbot.entity;

import java.util.Date;

public abstract class AbstractEntity {
    protected long id;
    protected long timestamp;
    protected Date creationTime;

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
