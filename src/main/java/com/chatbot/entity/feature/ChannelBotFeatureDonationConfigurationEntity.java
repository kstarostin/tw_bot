package com.chatbot.entity.feature;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "CHANNEL_BOT_FEATURE_DONATION_CONFIGURATION")
@PrimaryKeyJoinColumn(name = "CHANNEL_BOT_FEATURE_DONATION_CONFIGURATION_ID")
public class ChannelBotFeatureDonationConfigurationEntity extends ChannelBotFeatureConfigurationEntity implements Serializable {
}
