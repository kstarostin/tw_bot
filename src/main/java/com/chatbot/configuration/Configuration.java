package com.chatbot.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {

    private Map<String, String> bot;

    private Map<String, String> api;

    private Map<String, String> credentials;

    private boolean activeOnLiveStreamOnly;

    private List<String> activeFeatures;

    private int randomAliveTriggerProbability;

    private Map<String, String> channels;

    private List<String> messageWhitelistedPermissions;

    private int moderationWordNumberThreshold;

    private int moderationMaxWordNumber;

    public Map<String, String> getBot() {
        return bot;
    }

    public void setBot(Map<String, String> bot) {
        this.bot = bot;
    }

    public Map<String, String> getApi() {
        return api;
    }

    public void setApi(Map<String, String> api) {
        this.api = api;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    public boolean isActiveOnLiveStreamOnly() {
        return activeOnLiveStreamOnly;
    }

    public void setActiveOnLiveStreamOnly(boolean activeOnLiveStreamOnly) {
        this.activeOnLiveStreamOnly = activeOnLiveStreamOnly;
    }

    public List<String> getActiveFeatures() {
        return activeFeatures;
    }

    public void setActiveFeatures(List<String> activeFeatures) {
        this.activeFeatures = activeFeatures;
    }

    public int getRandomAliveTriggerProbability() {
        return randomAliveTriggerProbability;
    }

    public void setRandomAliveTriggerProbability(int randomAliveTriggerProbability) {
        this.randomAliveTriggerProbability = randomAliveTriggerProbability;
    }

    public Map<String, String> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, String> channels) {
        this.channels = channels;
    }

    public List<String> getMessageWhitelistedPermissions() {
        return messageWhitelistedPermissions;
    }

    public void setMessageWhitelistedPermissions(List<String> messageWhitelistedPermissions) {
        this.messageWhitelistedPermissions = messageWhitelistedPermissions;
    }

    public int getModerationWordNumberThreshold() {
        return moderationWordNumberThreshold;
    }

    public void setModerationWordNumberThreshold(int moderationWordNumberThreshold) {
        this.moderationWordNumberThreshold = moderationWordNumberThreshold;
    }

    public int getModerationMaxWordNumber() {
        return moderationMaxWordNumber;
    }

    public void setModerationMaxWordNumber(int moderationMaxWordNumber) {
        this.moderationMaxWordNumber = moderationMaxWordNumber;
    }
}
