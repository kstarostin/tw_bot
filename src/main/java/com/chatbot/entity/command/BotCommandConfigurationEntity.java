package com.chatbot.entity.command;

import com.chatbot.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "BOT_COMMAND_CONFIGURATION")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BotCommandConfigurationEntity extends AbstractEntity implements Serializable {
    private BotCommandTypeEntity commandType;
    private boolean isActive;
    private List<BotCommandTriggerEntity> commandTriggers;
    private List<BotCommandActionTypeEntity> availableCommandActionTypes;
    private List<BotCommandActionEntity> configuredCommandActions;

    public BotCommandConfigurationEntity() {
        this.creationTime = new Date();
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="BOT_COMMAND_TYPE_ID", referencedColumnName = "BOT_COMMAND_TYPE_ID")
    // one-to-one uni-directional
    public BotCommandTypeEntity getCommandType() {
        return commandType;
    }

    public void setCommandType(BotCommandTypeEntity commandType) {
        this.commandType = commandType;
    }

    @Column(name = "BOT_COMMAND_CONFIGURATION_IS_ACTIVE")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="BOT_COMMAND_CONFIGURATION_ID", referencedColumnName="BOT_COMMAND_CONFIGURATION_ID", nullable = false)
    // one-to-many unidirectional
    public List<BotCommandTriggerEntity> getCommandTriggers() {
        return commandTriggers;
    }

    public void setCommandTriggers(List<BotCommandTriggerEntity> commandTriggers) {
        this.commandTriggers = commandTriggers;
    }

    @ManyToMany
    @JoinTable(name = "BOT_COMMAND_CONFIGURATION_BOT_COMMAND_ACTION_TYPE",
            joinColumns = {@JoinColumn(name = "BOT_COMMAND_CONFIGURATION_ID", referencedColumnName = "BOT_COMMAND_CONFIGURATION_ID")},
            inverseJoinColumns = {@JoinColumn(name = "BOT_COMMAND_ACTION_TYPE_ID", referencedColumnName = "BOT_COMMAND_ACTION_TYPE_ID")})
    // many-to-many unidirectional
    public List<BotCommandActionTypeEntity> getAvailableCommandActionTypes() {
        return availableCommandActionTypes;
    }

    public void setAvailableCommandActionTypes(List<BotCommandActionTypeEntity> availableCommandActionTypes) {
        this.availableCommandActionTypes = availableCommandActionTypes;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="BOT_COMMAND_CONFIGURATION_ID", referencedColumnName="BOT_COMMAND_CONFIGURATION_ID", nullable = false)
    // one-to-many unidirectional
    public List<BotCommandActionEntity> getConfiguredCommandActions() {
        return configuredCommandActions;
    }

    public void setConfiguredCommandActions(List<BotCommandActionEntity> configuredCommandActions) {
        this.configuredCommandActions = configuredCommandActions;
    }

    @Id
    @Column(name = "BOT_COMMAND_CONFIGURATION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "BOT_COMMAND_CONFIGURATION_TIMESTAMP")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "BOT_COMMAND_CONFIGURATION_CREATION_TIME")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
