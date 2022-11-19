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
    private String botName;
    private String superAdmin;
    private List<String> twitchChannels;

    private Map<String, Configuration> channelConfigurations = new HashMap<>();

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
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
