package com.chatbot.entity.feature;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "CHANNEL_BOT_FEATURE_ALIVE_CONFIGURATION")
@PrimaryKeyJoinColumn(name = "CHANNEL_BOT_FEATURE_ALIVE_CONFIGURATION_ID")
public class ChannelBotFeatureAliveConfigurationEntity extends ChannelBotFeatureConfigurationEntity implements Serializable {
}
