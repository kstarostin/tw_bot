package com.chatbot.service;

import com.chatbot.entity.ChannelEntity;

import java.util.Optional;

public interface ChannelService {
    ChannelEntity createChannel(ChannelEntity entity);
    Optional<ChannelEntity> getChannel(long entityId);
    Optional<ChannelEntity> getChannelByName(String channelName);
    ChannelEntity updateChannel(ChannelEntity entity);
    void deleteChannel(ChannelEntity entity);
    boolean isUserIgnored(String channelName, String userName);
    boolean isUserSuperAdmin(String userName);
}
