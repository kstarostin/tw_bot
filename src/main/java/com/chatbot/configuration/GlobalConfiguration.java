package com.chatbot.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalConfiguration {
    private String twitchBotName;
    private String twitchBotId;
    private String discordBotName;
    private String discordBotDiscriminator;
    private String superAdmin;
    private List<String> twitchChannels;

    private Map<String, Configuration> channelConfigurations = new HashMap<>();

    public String getTwitchBotName() {
        return twitchBotName;
    }

    public void setTwitchBotName(String twitchBotName) {
        this.twitchBotName = twitchBotName;
    }

    public String getTwitchBotId() {
        return twitchBotId;
    }

    public void setTwitchBotId(String twitchBotId) {
        this.twitchBotId = twitchBotId;
    }

    public String getDiscordBotName() {
        return discordBotName;
    }

    public void setDiscordBotName(String discordBotName) {
        this.discordBotName = discordBotName;
    }

    public String getDiscordBotDiscriminator() {
        return discordBotDiscriminator;
    }

    public void setDiscordBotDiscriminator(String discordBotDiscriminator) {
        this.discordBotDiscriminator = discordBotDiscriminator;
    }

    public String getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(String superAdmin) {
        this.superAdmin = superAdmin;
    }

    public List<String> getTwitchChannels() {
        return twitchChannels;
    }

    public void setTwitchChannels(List<String> twitchChannels) {
        this.twitchChannels = twitchChannels;
    }

    public Map<String, Configuration> getChannelConfigurations() {
        return channelConfigurations;
    }

    public void setChannelConfigurations(Map<String, Configuration> channelConfigurations) {
        this.channelConfigurations = channelConfigurations;
    }
}
