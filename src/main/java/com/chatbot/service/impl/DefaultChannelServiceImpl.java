package com.chatbot.service.impl;

import com.chatbot.dao.ChannelDAO;
import com.chatbot.dao.impl.DefaultChannelDAOImpl;
import com.chatbot.entity.config.ChannelConfigurationEntity;
import com.chatbot.entity.ChannelEntity;
import com.chatbot.service.ChannelService;
import com.chatbot.service.GlobalConfigurationService;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.NoResultException;
import java.util.Optional;

public class DefaultChannelServiceImpl implements ChannelService {
    private static DefaultChannelServiceImpl instance;

    private final ChannelDAO channelDAO = DefaultChannelDAOImpl.getInstance();
    private final GlobalConfigurationService globalConfigurationService = DefaultGlobalConfigurationServiceImpl.getInstance();

    private DefaultChannelServiceImpl () {
    }

    public static synchronized DefaultChannelServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChannelServiceImpl();
        }
        return instance;
    }

    @Override
    public ChannelEntity createChannel(ChannelEntity entity) {
        return channelDAO.create(entity);
    }

    @Override
    public Optional<ChannelEntity> getChannel(long entityId) {
        return Optional.of(channelDAO.read(entityId));
    }

    @Override
    public Optional<ChannelEntity> getChannelByName(String channelName) {
        try {
            return Optional.of(channelDAO.getChannelByName(channelName));
        } catch (final NoResultException nre) {
            // todo log
            return Optional.empty();
        }
    }

    @Override
    public ChannelEntity updateChannel(ChannelEntity entity) {
        return channelDAO.update(entity);
    }

    @Override
    public void deleteChannel(ChannelEntity entity) {
        channelDAO.delete(entity);
    }

    @Override
    public boolean isUserIgnored(final String channelName, final String userName) {
        if (globalConfigurationService.isUserIgnored(userName)) {
            return true;
        }
        final Optional<ChannelConfigurationEntity> channelConfigurationEntityOptional = getChannelByName(channelName).map(ChannelEntity::getChannelConfiguration);
        return channelConfigurationEntityOptional.isPresent() && CollectionUtils.isNotEmpty(channelConfigurationEntityOptional.get().getIgnoredUsers())
                && channelConfigurationEntityOptional.get().getIgnoredUsers().stream().anyMatch(ignoredUserEntity -> ignoredUserEntity.getName().equalsIgnoreCase(channelName));
    }

    @Override
    public boolean isUserSuperAdmin(final String userName) {
        return globalConfigurationService.getSuperAdminName().map(userName::equalsIgnoreCase).orElse(false);
    }
}
