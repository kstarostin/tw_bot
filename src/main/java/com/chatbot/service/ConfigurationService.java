package com.chatbot.service;

import com.chatbot.configuration.Configuration;
import com.chatbot.configuration.GlobalConfiguration;

import java.util.Properties;

public interface ConfigurationService {
    GlobalConfiguration getConfiguration();
    Configuration getConfiguration(String channelName);

    void loadConfiguration();

    void loadConfiguration(String channelName);

    Properties getCredentialProperties();

    Properties getProperties(String path);

    String getBotName();

    String getSuperAdminName();

    boolean isActiveOnLiveStreamOnly(String channelName);
}
