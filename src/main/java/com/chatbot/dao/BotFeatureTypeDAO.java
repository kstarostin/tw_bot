package com.chatbot.dao;

import com.chatbot.entity.feature.BotFeatureTypeEntity;
import com.chatbot.util.FeatureEnum;

import java.util.Optional;

public interface BotFeatureTypeDAO extends CommonDAO<BotFeatureTypeEntity> {
    BotFeatureTypeEntity getBotFeatureTypeByEnum(FeatureEnum featureEnum);
    BotFeatureTypeEntity getBotFeatureTypeByCode(String code);
}
