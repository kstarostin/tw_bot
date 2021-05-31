package com.chatbot.dao.impl;

import com.chatbot.dao.GlobalConfigurationDAO;
import com.chatbot.entity.config.GlobalConfigurationEntity;

public class DefaultGlobalConfigurationDAOImpl extends AbstractDAO<GlobalConfigurationEntity> implements GlobalConfigurationDAO {
    private static DefaultGlobalConfigurationDAOImpl instance;

    private DefaultGlobalConfigurationDAOImpl () {
    }

    public static synchronized DefaultGlobalConfigurationDAOImpl getInstance() {
        if (instance == null) {
            instance = new DefaultGlobalConfigurationDAOImpl();
        }
        return instance;
    }

    @Override
    public GlobalConfigurationEntity getGlobalConfigurationByCode(final String code) {
        return getByCode(code);
    }
}
