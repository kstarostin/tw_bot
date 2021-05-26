package com.chatbot.entity.feature;

import com.chatbot.entity.command.BotCommandConfigurationEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "CHANNEL_BOT_FEATURE_COMMAND_CONFIGURATION")
@PrimaryKeyJoinColumn(name = "CHANNEL_BOT_FEATURE_COMMAND_CONFIGURATION_ID")
public class ChannelBotFeatureCommandConfigurationEntity extends ChannelBotFeatureConfigurationEntity implements Serializable {
    private Set<BotCommandConfigurationEntity> configuredChannelCommands;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="CHANNEL_BOT_FEATURE_COMMAND_CONFIGURATION_ID", referencedColumnName="CHANNEL_BOT_FEATURE_COMMAND_CONFIGURATION_ID", nullable = false)
    // one-to-many unidirectional
    public Set<BotCommandConfigurationEntity> getConfiguredChannelCommands() {
        return configuredChannelCommands;
    }

    public void setConfiguredChannelCommands(Set<BotCommandConfigurationEntity> configuredChannelCommands) {
        this.configuredChannelCommands = configuredChannelCommands;
    }
}
