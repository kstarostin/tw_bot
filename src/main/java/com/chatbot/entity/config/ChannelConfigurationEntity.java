package com.chatbot.entity.config;

import com.chatbot.entity.AbstractEntity;
import com.chatbot.entity.feature.BotFeatureTypeEntity;
import com.chatbot.entity.ChannelEntity;
import com.chatbot.entity.feature.ChannelBotFeatureConfigurationEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "CHANNEL_CONFIG")
public class ChannelConfigurationEntity extends AbstractEntity implements Serializable {
    private ChannelEntity channel;
    private Set<ChannelEntity> channelBotAdmins;
    private Set<BotFeatureTypeEntity> availableChannelFeatureTypes;
    private Set<ChannelBotFeatureConfigurationEntity> configuredChannelFeatures;
    private Set<ChannelEntity> ignoredUsers;

    public ChannelConfigurationEntity() {
        this.creationTime = new Date();
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "channelConfiguration")
    // one-to-one bidirectional
    public ChannelEntity getChannel() {
        return channel;
    }

    public void setChannel(ChannelEntity channel) {
        this.channel = channel;
    }

    @ManyToMany
    @JoinTable(name = "CHANNEL_CONFIG_CHANNEL_BOT_ADMIN",
            joinColumns = {@JoinColumn(name = "CHANNEL_CONFIG_ID", referencedColumnName = "CHANNEL_CONFIG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID")})
    // many-to-many bidirectional
    public Set<ChannelEntity> getChannelBotAdmins() {
        return channelBotAdmins;
    }

    public void setChannelBotAdmins(Set<ChannelEntity> channelAdministrators) {
        this.channelBotAdmins = channelAdministrators;
    }

    @ManyToMany
    @JoinTable(name = "CHANNEL_CONFIG_AVAILABLE_CHANNEL_FEATURE",
            joinColumns = {@JoinColumn(name = "CHANNEL_CONFIG_ID", referencedColumnName = "CHANNEL_CONFIG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "BOT_FEATURE_TYPE_ID", referencedColumnName = "BOT_FEATURE_TYPE_ID")})
    // many-to-many unidirectional
    public Set<BotFeatureTypeEntity> getAvailableChannelFeatureTypes() {
        return availableChannelFeatureTypes;
    }

    public void setAvailableChannelFeatureTypes(Set<BotFeatureTypeEntity> availableChannelFeatureTypes) {
        this.availableChannelFeatureTypes = availableChannelFeatureTypes;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="CHANNEL_CONFIG_ID", referencedColumnName="CHANNEL_CONFIG_ID", nullable = false)
    // one-to-many unidirectional
    public Set<ChannelBotFeatureConfigurationEntity> getConfiguredChannelFeatures() {
        return configuredChannelFeatures;
    }

    public void setConfiguredChannelFeatures(Set<ChannelBotFeatureConfigurationEntity> configuredChannelFeatures) {
        this.configuredChannelFeatures = configuredChannelFeatures;
    }

    @ManyToMany
    @JoinTable(name = "CHANNEL_CONFIG_IGNORED_USER",
            joinColumns = {@JoinColumn(name = "CHANNEL_CONFIG_ID", referencedColumnName = "CHANNEL_CONFIG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID")})
    // many-to-many unidirectional
    public Set<ChannelEntity> getIgnoredUsers() {
        return ignoredUsers;
    }

    public void setIgnoredUsers(Set<ChannelEntity> ignoredUsers) {
        this.ignoredUsers = ignoredUsers;
    }

    @Id
    @Column(name = "CHANNEL_CONFIG_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "CHANNEL_CONFIG_TIMESTAMP")
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "CHANNEL_CONFIG_CREATION_TIME")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
