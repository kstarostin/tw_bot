package com.chatbot.dao.impl;

import com.chatbot.dao.GlobalConfigurationDAO;
import com.chatbot.entity.config.GlobalConfigurationEntity;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

public class DefaultGlobalConfigurationDAOImpl extends AbstractDAO<GlobalConfigurationEntity> implements GlobalConfigurationDAO {
    private static DefaultGlobalConfigurationDAOImpl instance;

    private DefaultGlobalConfigurationDAOImpl () {
    }

    public static synchronized DefaultGlobalConfigurationDAOImpl getInstance() {
        if (instance == null) {
            instance = new DefaultGlobalConfigurationDAOImpl();
        }
        return instance;
    }

    @Override
    public GlobalConfigurationEntity getGlobalConfigurationByCode(final String code) {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            if (!entityTransaction.isActive()) {
                entityTransaction.begin();
            }
            final Query query = entityManager.createNamedQuery("GlobalConfigurationEntity.getConfigByCode");
            query.setParameter("code", code);
            return (GlobalConfigurationEntity) query.getSingleResult();
        }
        catch (RuntimeException re) {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw re;
        } finally {
            if (entityTransaction.isActive()) {
                entityTransaction.commit();
            }
        }
    }
}
