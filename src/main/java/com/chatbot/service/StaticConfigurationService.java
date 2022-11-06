package com.chatbot.service;

import com.chatbot.configuration.Configuration;

import java.util.Properties;

public interface StaticConfigurationService {
    Configuration getStaticConfiguration();

    void loadInitialStaticConfiguration();

    Properties getCredentialProperties();

    Properties getProperties(String path);

    String getBotName();

    String getSuperAdminName();

    boolean isActiveOnLiveStreamOnly();
}
