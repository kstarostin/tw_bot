package com.chatbot.entity.command;

import com.chatbot.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "BOT_COMMAND_ACTION")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BotCommandActionEntity extends AbstractEntity implements Serializable {
    private BotCommandActionTypeEntity commandActionType;

    public BotCommandActionEntity() {
        this.creationTime = new Date();
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="BOT_COMMAND_ACTION_TYPE_ID", referencedColumnName = "BOT_COMMAND_ACTION_TYPE_ID")
    // one-to-one uni-directional
    public BotCommandActionTypeEntity getCommandActionType() {
        return commandActionType;
    }

    public void setCommandActionType(BotCommandActionTypeEntity commandActionType) {
        this.commandActionType = commandActionType;
    }

    @Id
    @Column(name = "BOT_COMMAND_ACTION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "BOT_COMMAND_ACTION_TIMESTAMP")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "BOT_COMMAND_ACTION_CREATION_TIME")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
