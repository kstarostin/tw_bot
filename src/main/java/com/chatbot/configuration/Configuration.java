package com.chatbot.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {

    private Boolean debug;

    private Map<String, String> bot;

    private Map<String, String> api;

    private Map<String, String> credentials;

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

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

    @Override
    public String toString() {
        return "StaticConfiguration {" +
                "bot=" + bot +
                ", api=" + api +
                ", credentials=" + credentials +
                '}';
    }
}
