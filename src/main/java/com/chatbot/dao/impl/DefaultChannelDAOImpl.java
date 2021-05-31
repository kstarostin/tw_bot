package com.chatbot.dao.impl;

import com.chatbot.dao.ChannelDAO;
import com.chatbot.entity.ChannelEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

public class DefaultChannelDAOImpl extends AbstractDAO<ChannelEntity> implements ChannelDAO {
    private final Logger LOG = LoggerFactory.getLogger(DefaultChannelDAOImpl.class);

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
        LOG.debug("Get entity [{}] by name [{}] ...", ChannelEntity.class, name);
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            beginTransaction(entityTransaction);
            final Query query = entityManager.createNamedQuery("ChannelEntity.getChannelByName");
            query.setParameter("name", name);
            final ChannelEntity channelEntity = (ChannelEntity) query.getSingleResult();
            LOG.debug("Found entity [{}] with id [{}], timestamp [{}]", ChannelEntity.class, channelEntity.getId(), channelEntity.getTimestamp());
            return channelEntity;
        }
        catch (RuntimeException re) {
            LOG.debug("Unexpected error during reading entity [{}] by name [{}] ...", ChannelEntity.class, name, re);
            rollbackTransaction(entityTransaction);
            throw re;
        } finally {
            commitTransaction(entityTransaction);
        }
    }
}
