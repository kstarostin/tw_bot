package com.chatbot.dao;

import com.chatbot.entity.command.BotCommandActionTypeEntity;
import com.chatbot.util.BotCommandActionEnum;

public interface BotCommandActionTypeDAO extends CommonDAO<BotCommandActionTypeEntity> {
    BotCommandActionTypeEntity getBotCommandActionTypeByEnum(BotCommandActionEnum botCommandActionEnum);
    BotCommandActionTypeEntity getBotCommandActionTypeByCode(String code);
}
