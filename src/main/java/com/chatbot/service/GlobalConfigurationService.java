package com.chatbot.service;

import com.chatbot.entity.config.GlobalConfigurationEntity;

import java.util.Optional;

public interface GlobalConfigurationService {
    GlobalConfigurationEntity createGlobalConfiguration(GlobalConfigurationEntity entity);
    Optional<GlobalConfigurationEntity> getGlobalConfiguration(long entityId);
    Optional<GlobalConfigurationEntity> getGlobalConfigurationByCode(String code);
    Optional<GlobalConfigurationEntity> getCurrentGlobalConfiguration();
    GlobalConfigurationEntity updateGlobalConfiguration(GlobalConfigurationEntity entity);
    void deleteGlobalConfiguration(GlobalConfigurationEntity entity);
    boolean isUserIgnored(String userName);
    Optional<String> getSuperAdminName();
}
