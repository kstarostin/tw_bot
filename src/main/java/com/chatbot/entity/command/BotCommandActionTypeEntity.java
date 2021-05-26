package com.chatbot.entity.command;

import com.chatbot.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "BOT_COMMAND_ACTION_TYPE")
@NamedQueries(
        {
                @NamedQuery(name = "BotCommandActionTypeEntity.getByCode", query = "SELECT actionType FROM BotCommandActionTypeEntity actionType WHERE actionType.code = :code"),
        })
public class BotCommandActionTypeEntity extends AbstractEntity implements Serializable {
    private String code; // BotCommandActionEnum

    public BotCommandActionTypeEntity() {
        this.creationTime = new Date();
    }

    @Column(name = "BOT_COMMAND_ACTION_TYPE_CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Id
    @Column(name = "BOT_COMMAND_ACTION_TYPE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "BOT_COMMAND_ACTION_TYPE_TIMESTAMP")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "BOT_COMMAND_ACTION_TYPE_CREATION_TIME")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
