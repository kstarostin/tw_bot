package com.chatbot.dao.impl;

import com.chatbot.dao.BotFeatureTypeDAO;
import com.chatbot.entity.feature.BotFeatureTypeEntity;
import com.chatbot.util.FeatureEnum;

public class DefaultBotFeatureTypeDAOImpl extends AbstractDAO<BotFeatureTypeEntity> implements BotFeatureTypeDAO {
    private static DefaultBotFeatureTypeDAOImpl instance;

    private DefaultBotFeatureTypeDAOImpl() {
    }

    public static synchronized DefaultBotFeatureTypeDAOImpl getInstance() {
        if (instance == null) {
            instance = new DefaultBotFeatureTypeDAOImpl();
        }
        return instance;
    }

    @Override
    public BotFeatureTypeEntity getBotFeatureTypeByEnum(final FeatureEnum featureEnum) {
        return getBotFeatureTypeByCode(featureEnum.toString());
    }

    @Override
    public BotFeatureTypeEntity getBotFeatureTypeByCode(final String code) {
        return getByCode(code);
    }
}
