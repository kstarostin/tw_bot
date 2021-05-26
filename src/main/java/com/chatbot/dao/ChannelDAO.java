package com.chatbot.dao;

import com.chatbot.entity.ChannelEntity;

public interface ChannelDAO extends CommonDAO<ChannelEntity> {

    ChannelEntity getChannelByName(String name);
}
