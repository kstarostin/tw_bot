package com.chatbot.dao.impl;

import com.chatbot.dao.BotCommandTypeDAO;
import com.chatbot.entity.command.BotCommandTypeEntity;
import com.chatbot.util.BotCommandEnum;

public class DefaultBotCommandTypeDAOImpl extends AbstractDAO<BotCommandTypeEntity> implements BotCommandTypeDAO {
    private static DefaultBotCommandTypeDAOImpl instance;

    private DefaultBotCommandTypeDAOImpl() {
    }

    public static synchronized DefaultBotCommandTypeDAOImpl getInstance() {
        if (instance == null) {
            instance = new DefaultBotCommandTypeDAOImpl();
        }
        return instance;
    }

    @Override
    public BotCommandTypeEntity getBotCommandTypeByEnum(final BotCommandEnum botCommandEnum) {
        return getBotCommandTypeByCode(botCommandEnum.toString());
    }

    @Override
    public BotCommandTypeEntity getBotCommandTypeByCode(final String code) {
        return getByCode(code);
    }
}
