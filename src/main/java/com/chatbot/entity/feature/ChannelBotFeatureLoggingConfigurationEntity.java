package com.chatbot.entity.feature;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "CHANNEL_BOT_FEATURE_LOGGING_CONFIGURATION")
@PrimaryKeyJoinColumn(name = "CHANNEL_BOT_FEATURE_LOGGING_CONFIGURATION_ID")
public class ChannelBotFeatureLoggingConfigurationEntity extends ChannelBotFeatureConfigurationEntity implements Serializable {
}
