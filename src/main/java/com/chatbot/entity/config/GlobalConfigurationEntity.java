package com.chatbot.entity.config;

import com.chatbot.entity.AbstractEntity;
import com.chatbot.entity.feature.BotFeatureTypeEntity;
import com.chatbot.entity.ChannelEntity;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "GLOBAL_CONFIG")
@NamedQueries(
        {
                @NamedQuery(name = "GlobalConfigurationEntity.getConfigByCode", query = "SELECT config FROM GlobalConfigurationEntity config WHERE config.code = :code"),
        })
public class GlobalConfigurationEntity extends AbstractEntity implements Serializable {
    private String code; // bot user name
    private ChannelEntity superAdmin;
    private Set<ChannelEntity> globalBotAdmins;
    private Set<BotFeatureTypeEntity> activeGlobalFeatureTypes;
    private List<ChannelEntity> configuredChannels;
    private Set<ChannelEntity> ignoredUsers;

    public GlobalConfigurationEntity() {
        this.creationTime = new Date();
    }

    @Column(name = "GLOBAL_CONFIG_CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLOBAL_CONFIG_SUPER_ADMIN_ID")
    // many-to-one bidirectional
    public ChannelEntity getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(ChannelEntity superAdmin) {
        if (this.superAdmin != null && CollectionUtils.isNotEmpty(this.superAdmin.getAvailableGlobalConfigurationsAsSuperAdmin())) {
            this.superAdmin.getAvailableGlobalConfigurationsAsSuperAdmin().remove(this);
        }
        this.superAdmin = superAdmin;
        if (superAdmin != null) {
            if (CollectionUtils.isEmpty(superAdmin.getAvailableGlobalConfigurationsAsSuperAdmin())) {
                superAdmin.setAvailableGlobalConfigurationsAsSuperAdmin(new ArrayList<>());
            }
            superAdmin.getAvailableGlobalConfigurationsAsSuperAdmin().add(this);
        }
    }

    @ManyToMany
    @JoinTable(name = "GLOBAL_CONFIG_GLOBAL_BOT_ADMIN",
            joinColumns = {@JoinColumn(name = "GLOBAL_CONFIG_ID", referencedColumnName = "GLOBAL_CONFIG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID")})
    // many-to-many bidirectional
    public Set<ChannelEntity> getGlobalBotAdmins() {
        return globalBotAdmins;
    }

    public void setGlobalBotAdmins(Set<ChannelEntity> globalBotAdmins) {
        this.globalBotAdmins = globalBotAdmins;
    }

    public void addGlobalBotAdmins(ChannelEntity globalBotAdmin) {
        this.globalBotAdmins.add(globalBotAdmin);
        globalBotAdmin.getAvailableGlobalConfigurationsAsAdmin().add(this);
    }

    public void removeGlobalBotAdmins(ChannelEntity globalBotAdmin) {
        this.globalBotAdmins.remove(globalBotAdmin);
        globalBotAdmin.getAvailableGlobalConfigurationsAsAdmin().remove(this);
    }

    @ManyToMany
    @JoinTable(name = "GLOBAL_CONFIG_GLOBAL_FEATURE",
            joinColumns = {@JoinColumn(name = "GLOBAL_CONFIG_ID", referencedColumnName = "GLOBAL_CONFIG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "BOT_FEATURE_TYPE_ID", referencedColumnName = "BOT_FEATURE_TYPE_ID")})
    // many-to-many unidirectional
    public Set<BotFeatureTypeEntity> getActiveGlobalFeatureTypes() {
        return activeGlobalFeatureTypes;
    }

    public void setActiveGlobalFeatureTypes(Set<BotFeatureTypeEntity> activeGlobalFeatureTypes) {
        this.activeGlobalFeatureTypes = activeGlobalFeatureTypes;
    }

    public void addActiveGlobalFeatureTypes(BotFeatureTypeEntity featureType) {
        this.activeGlobalFeatureTypes.add(featureType);
    }

    public void removeActiveGlobalFeatureTypes(BotFeatureTypeEntity featureType) {
        this.activeGlobalFeatureTypes.remove(featureType);
    }

    @OneToMany(mappedBy = "globalConfiguration")
    // one-to-many bidirectional
    public List<ChannelEntity> getConfiguredChannels() {
        return configuredChannels;
    }

    public void setConfiguredChannels(List<ChannelEntity> configuredChannels) {
        this.configuredChannels = configuredChannels;
    }

    @ManyToMany
    @JoinTable(name = "GLOBAL_CONFIG_IGNORED_USER",
            joinColumns = {@JoinColumn(name = "GLOBAL_CONFIG_ID", referencedColumnName = "GLOBAL_CONFIG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID")})
    // many-to-many unidirectional
    public Set<ChannelEntity> getIgnoredUsers() {
        return ignoredUsers;
    }

    public void setIgnoredUsers(Set<ChannelEntity> ignoredUsers) {
        this.ignoredUsers = ignoredUsers;
    }

    @Id
    @Column(name = "GLOBAL_CONFIG_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "GLOBAL_CONFIG_TIMESTAMP")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "GLOBAL_CONFIG_CREATION_TIME")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
