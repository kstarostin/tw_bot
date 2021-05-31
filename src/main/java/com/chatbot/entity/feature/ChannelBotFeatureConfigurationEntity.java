package com.chatbot.entity.feature;

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
@Table(name = "CHANNEL_BOT_FEATURE_CONFIGURATION")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ChannelBotFeatureConfigurationEntity extends AbstractEntity implements Serializable {
    private BotFeatureTypeEntity featureType;
    private boolean isActive;

    public ChannelBotFeatureConfigurationEntity() {
        this.creationTime = new Date();
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="BOT_FEATURE_TYPE_ID", referencedColumnName = "BOT_FEATURE_TYPE_ID")
    // one-to-one uni-directional
    public BotFeatureTypeEntity getFeatureType() {
        return featureType;
    }

    public void setFeatureType(BotFeatureTypeEntity featureType) {
        this.featureType = featureType;
    }

    @Column(name = "CHANNEL_BOT_FEATURE_CONFIGURATION_IS_ACTIVE")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Id
    @Column(name = "CHANNEL_BOT_FEATURE_CONFIGURATION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "CHANNEL_BOT_FEATURE_CONFIGURATION_TIMESTAMP")
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "CHANNEL_BOT_FEATURE_CONFIGURATION_CREATION_TIME")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
