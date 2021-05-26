package com.chatbot.dao;

import com.chatbot.entity.config.GlobalConfigurationEntity;

public interface GlobalConfigurationDAO extends CommonDAO<GlobalConfigurationEntity> {

    GlobalConfigurationEntity getGlobalConfigurationByCode(String code);
}
