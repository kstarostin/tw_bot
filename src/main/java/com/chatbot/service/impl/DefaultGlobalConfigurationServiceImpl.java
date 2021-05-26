package com.chatbot.service.impl;

import com.chatbot.dao.GlobalConfigurationDAO;
import com.chatbot.dao.impl.DefaultGlobalConfigurationDAOImpl;
import com.chatbot.entity.ChannelEntity;
import com.chatbot.entity.config.GlobalConfigurationEntity;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.GlobalConfigurationService;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.NoResultException;
import java.util.Optional;

public class DefaultGlobalConfigurationServiceImpl implements GlobalConfigurationService {
    private static DefaultGlobalConfigurationServiceImpl instance;

    private static final String STATIC_CONFIG_ATTR_NAME = "name";

    private final StaticConfigurationService staticConfigurationService = DefaultStaticConfigurationServiceImpl.getInstance();
    private final GlobalConfigurationDAO globalConfigurationDAO = DefaultGlobalConfigurationDAOImpl.getInstance();

    private DefaultGlobalConfigurationServiceImpl () {
    }

    public static synchronized DefaultGlobalConfigurationServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultGlobalConfigurationServiceImpl();
        }
        return instance;
    }

    @Override
    public GlobalConfigurationEntity createGlobalConfiguration(GlobalConfigurationEntity entity) {
        return globalConfigurationDAO.create(entity);
    }

    @Override
    public Optional<GlobalConfigurationEntity> getGlobalConfiguration(long entityId) {
        return Optional.of(globalConfigurationDAO.read(entityId));
    }

    @Override
    public Optional<GlobalConfigurationEntity> getGlobalConfigurationByCode(String code) {
        try {
            return Optional.of(globalConfigurationDAO.getGlobalConfigurationByCode(code));
        } catch (final NoResultException nre) {
            // todo log
            return Optional.empty();
        }
    }

    @Override
    public Optional<GlobalConfigurationEntity> getCurrentGlobalConfiguration() {
        final String botName = staticConfigurationService.getStaticConfiguration().getBot().get(STATIC_CONFIG_ATTR_NAME);
        return getGlobalConfigurationByCode(botName);
    }

    @Override
    public GlobalConfigurationEntity updateGlobalConfiguration(GlobalConfigurationEntity entity) {
        return globalConfigurationDAO.update(entity);
    }

    @Override
    public void deleteGlobalConfiguration(GlobalConfigurationEntity entity) {
        globalConfigurationDAO.delete(entity);
    }

    @Override
    public boolean isUserIgnored(final String userName) {
        final Optional<GlobalConfigurationEntity> globalConfigurationEntityOptional = getCurrentGlobalConfiguration();
        return globalConfigurationEntityOptional.isPresent()
                && CollectionUtils.isNotEmpty(globalConfigurationEntityOptional.get().getIgnoredUsers())
                && globalConfigurationEntityOptional.get().getIgnoredUsers().stream().anyMatch(ignoredUserEntity -> ignoredUserEntity.getName().equalsIgnoreCase(userName));
    }

    @Override
    public Optional<String> getSuperAdminName() {
        return getCurrentGlobalConfiguration()
                .map(GlobalConfigurationEntity::getSuperAdmin)
                .map(ChannelEntity::getName);
    }
}
