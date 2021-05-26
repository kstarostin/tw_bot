package com.chatbot.dao;

import com.chatbot.entity.command.BotCommandTypeEntity;
import com.chatbot.util.BotCommandEnum;

public interface BotCommandTypeDAO extends CommonDAO<BotCommandTypeEntity> {
    BotCommandTypeEntity getBotCommandTypeByEnum(BotCommandEnum botCommandEnum);
    BotCommandTypeEntity getBotCommandTypeByCode(String code);
}
