package com.chatbot.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Set;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {
    private boolean activeOnLiveStreamOnly;

    private boolean muted;
    private Set<String> activeFeatures;
    private boolean userGreetingEnabled;
    private int independenceRate;
    private List<String> messageWhitelistedPermissions;
    private boolean checkModeratorPermissions;
    private int moderationWordNumberThreshold;
    private int violationPointsThresholdForTimeout;
    private int violationPointsThresholdForBan;
    private int violationPointsForFirstMessage;
    private int autoTimeoutTimeSeconds;

    public boolean isActiveOnLiveStreamOnly() {
        return activeOnLiveStreamOnly;
    }

    public void setActiveOnLiveStreamOnly(boolean activeOnLiveStreamOnly) {
        this.activeOnLiveStreamOnly = activeOnLiveStreamOnly;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public Set<String> getActiveFeatures() {
        return activeFeatures;
    }

    public void setActiveFeatures(Set<String> activeFeatures) {
        this.activeFeatures = activeFeatures;
    }

    public boolean isUserGreetingEnabled() {
        return userGreetingEnabled;
    }

    public void setUserGreetingEnabled(boolean userGreetingEnabled) {
        this.userGreetingEnabled = userGreetingEnabled;
    }

    public int getIndependenceRate() {
        return independenceRate;
    }

    public void setIndependenceRate(int independenceRate) {
        this.independenceRate = independenceRate;
    }

    public List<String> getMessageWhitelistedPermissions() {
        return messageWhitelistedPermissions;
    }

    public void setMessageWhitelistedPermissions(List<String> messageWhitelistedPermissions) {
        this.messageWhitelistedPermissions = messageWhitelistedPermissions;
    }

    public boolean isCheckModeratorPermissions() {
        return checkModeratorPermissions;
    }

    public void setCheckModeratorPermissions(boolean checkModeratorPermissions) {
        this.checkModeratorPermissions = checkModeratorPermissions;
    }

    public int getModerationWordNumberThreshold() {
        return moderationWordNumberThreshold;
    }

    public void setModerationWordNumberThreshold(int moderationWordNumberThreshold) {
        this.moderationWordNumberThreshold = moderationWordNumberThreshold;
    }

    public int getViolationPointsThresholdForTimeout() {
        return violationPointsThresholdForTimeout;
    }

    public void setViolationPointsThresholdForTimeout(int violationPointsThresholdForTimeout) {
        this.violationPointsThresholdForTimeout = violationPointsThresholdForTimeout;
    }

    public int getViolationPointsThresholdForBan() {
        return violationPointsThresholdForBan;
    }

    public void setViolationPointsThresholdForBan(int violationPointsThresholdForBan) {
        this.violationPointsThresholdForBan = violationPointsThresholdForBan;
    }

    public int getViolationPointsForFirstMessage() {
        return violationPointsForFirstMessage;
    }

    public void setViolationPointsForFirstMessage(int violationPointsForFirstMessage) {
        this.violationPointsForFirstMessage = violationPointsForFirstMessage;
    }

    public int getAutoTimeoutTimeSeconds() {
        return autoTimeoutTimeSeconds;
    }

    public void setAutoTimeoutTimeSeconds(int autoTimeoutTimeSeconds) {
        this.autoTimeoutTimeSeconds = autoTimeoutTimeSeconds;
    }
}
