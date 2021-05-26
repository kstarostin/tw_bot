package com.chatbot.dao.impl;

import com.chatbot.dao.ChannelDAO;
import com.chatbot.entity.ChannelEntity;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

public class DefaultChannelDAOImpl extends AbstractDAO<ChannelEntity> implements ChannelDAO {
    private static DefaultChannelDAOImpl instance;

    private DefaultChannelDAOImpl () {
    }

    public static synchronized DefaultChannelDAOImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChannelDAOImpl();
        }
        return instance;
    }

    @Override
    public ChannelEntity getChannelByName(final String name) {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            if (!entityTransaction.isActive()) {
                entityTransaction.begin();
            }
            final Query query = entityManager.createNamedQuery("ChannelEntity.getChannelByName");
            query.setParameter("name", name);
            return (ChannelEntity) query.getSingleResult();
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
