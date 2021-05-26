package com.chatbot.dao.impl;

import com.chatbot.dao.ChannelConfigurationDAO;
import com.chatbot.entity.config.ChannelConfigurationEntity;

public class DefaultChannelConfigurationDAOImpl extends AbstractDAO<ChannelConfigurationEntity> implements ChannelConfigurationDAO {
    private static DefaultChannelConfigurationDAOImpl instance;

    private DefaultChannelConfigurationDAOImpl () {
    }

    public static synchronized DefaultChannelConfigurationDAOImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChannelConfigurationDAOImpl();
        }
        return instance;
    }
}
