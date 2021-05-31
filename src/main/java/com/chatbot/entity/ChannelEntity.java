package com.chatbot.entity;

import com.chatbot.entity.config.ChannelConfigurationEntity;
import com.chatbot.entity.config.GlobalConfigurationEntity;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "CHANNEL")
@NamedQueries(
        {
                @NamedQuery(name = "ChannelEntity.getChannelByName", query = "SELECT channel FROM ChannelEntity channel WHERE channel.name = :name"),
        })
public class ChannelEntity extends AbstractEntity implements Serializable {
    private String name;
    private List<GlobalConfigurationEntity> availableGlobalConfigurationsAsSuperAdmin;
    private Set<GlobalConfigurationEntity> availableGlobalConfigurationsAsAdmin;
    private GlobalConfigurationEntity globalConfiguration;
    private ChannelConfigurationEntity channelConfiguration;
    private Set<ChannelConfigurationEntity> availableChannelConfigurationsAsAdmin;

    public ChannelEntity() {
        this.creationTime = new Date();
    }

    @Column(name = "CHANNEL_NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "superAdmin")
    // one-to-many bidirectional
    public List<GlobalConfigurationEntity> getAvailableGlobalConfigurationsAsSuperAdmin() {
        return availableGlobalConfigurationsAsSuperAdmin;
    }

    public void setAvailableGlobalConfigurationsAsSuperAdmin(List<GlobalConfigurationEntity> superAdminForGlobalConfigurationList) {
        this.availableGlobalConfigurationsAsSuperAdmin = superAdminForGlobalConfigurationList;
    }

    @ManyToMany(mappedBy = "globalBotAdmins")
    // many-to-many bidirectional
    public Set<GlobalConfigurationEntity> getAvailableGlobalConfigurationsAsAdmin() {
        return availableGlobalConfigurationsAsAdmin;
    }

    public void setAvailableGlobalConfigurationsAsAdmin(Set<GlobalConfigurationEntity> adminForGlobalConfigurationList) {
        this.availableGlobalConfigurationsAsAdmin = adminForGlobalConfigurationList;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLOBAL_CONFIG_ID", referencedColumnName = "GLOBAL_CONFIG_ID")
    // many-to-one bidirectional
    public GlobalConfigurationEntity getGlobalConfiguration() {
        return globalConfiguration;
    }

    public void setGlobalConfiguration(GlobalConfigurationEntity globalConfiguration) {
        if (this.globalConfiguration != null && CollectionUtils.isNotEmpty(this.globalConfiguration.getConfiguredChannels())) {
            this.globalConfiguration.getConfiguredChannels().remove(this);
        }
        this.globalConfiguration = globalConfiguration;
        if (globalConfiguration != null) {
            if (CollectionUtils.isEmpty(globalConfiguration.getConfiguredChannels())) {
                globalConfiguration.setConfiguredChannels(new ArrayList<>());
            }
            globalConfiguration.getConfiguredChannels().add(this);
        }
    }

    public void removeGlobalConfiguration() {
        if (this.globalConfiguration != null && CollectionUtils.isNotEmpty(this.globalConfiguration.getConfiguredChannels())) {
            this.globalConfiguration.getConfiguredChannels().remove(this);
        }
        this.globalConfiguration = null;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "CHANNEL_CONFIG_ID", referencedColumnName = "CHANNEL_CONFIG_ID")
    // one-to-one bidirectional
    public ChannelConfigurationEntity getChannelConfiguration() {
        return channelConfiguration;
    }

    public void setChannelConfiguration(ChannelConfigurationEntity channelConfiguration) {

        this.channelConfiguration = channelConfiguration;
    }

    public void addChannelConfiguration(ChannelConfigurationEntity channelConfiguration) {
        if (this.channelConfiguration != null && this.channelConfiguration.getChannel() != null) {
            this.channelConfiguration.setChannel(null);
        }
        this.channelConfiguration = channelConfiguration;
        if (channelConfiguration != null) {
            channelConfiguration.setChannel(this);
        }
    }

    public void removeChannelConfiguration() {
        if (this.channelConfiguration != null) {
            this.channelConfiguration.setChannel(null);
        }
        this.channelConfiguration = null;
    }

    @ManyToMany(mappedBy = "channelBotAdmins")
    // many-to-many bidirectional
    public Set<ChannelConfigurationEntity> getAvailableChannelConfigurationsAsAdmin() {
        return availableChannelConfigurationsAsAdmin;
    }

    public void setAvailableChannelConfigurationsAsAdmin(Set<ChannelConfigurationEntity> availableChannelConfigurationsAsAdmin) {
        this.availableChannelConfigurationsAsAdmin = availableChannelConfigurationsAsAdmin;
    }

    @Id
    @Column(name = "CHANNEL_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "CHANNEL_TIMESTAMP")
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "CHANNEL_CREATION_TIME")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
