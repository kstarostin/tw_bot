package com.chatbot.service;

import com.chatbot.configuration.Configuration;

public interface StaticConfigurationService {
    Configuration getStaticConfiguration();
    void loadInitialStaticConfiguration();

    String getBotName();

    String getSuperAdminName();

    boolean isActiveOnLiveStreamOnly();
}
