package com.chatbot.dao.impl;

import com.chatbot.dao.BotCommandActionTypeDAO;
import com.chatbot.entity.command.BotCommandActionTypeEntity;
import com.chatbot.util.BotCommandActionEnum;

public class DefaultBotCommandActionTypeDAOImpl extends AbstractDAO<BotCommandActionTypeEntity> implements BotCommandActionTypeDAO {
    private static DefaultBotCommandActionTypeDAOImpl instance;

    private DefaultBotCommandActionTypeDAOImpl() {
    }

    public static synchronized DefaultBotCommandActionTypeDAOImpl getInstance() {
        if (instance == null) {
            instance = new DefaultBotCommandActionTypeDAOImpl();
        }
        return instance;
    }

    @Override
    public BotCommandActionTypeEntity getBotCommandActionTypeByEnum(final BotCommandActionEnum botCommandActionEnum) {
        return getBotCommandActionTypeByCode(botCommandActionEnum.toString());
    }

    @Override
    public BotCommandActionTypeEntity getBotCommandActionTypeByCode(final String code) {
        return getByCode(code);
    }
}
