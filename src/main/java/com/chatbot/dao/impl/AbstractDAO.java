package com.chatbot.dao.impl;

import com.chatbot.dao.CommonDAO;
import com.chatbot.entity.AbstractEntity;
import com.chatbot.entitymanager.JpaEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;

public class AbstractDAO<T extends AbstractEntity> implements CommonDAO<T> {
    private final Logger LOG = LoggerFactory.getLogger(AbstractDAO.class);

    private final Class<T> type;
    private final String entityName;

    protected final EntityManager entityManager = JpaEntityManager.getEntityManager();

    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityName = type.getSimpleName();
    }

    @Override
    public T create(final T t) {
        LOG.debug("Create entity [{}] ...", entityName);
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            beginTransaction(entityTransaction);
            entityManager.persist(t);
            LOG.debug("Created entity [{}] with id [{}], timestamp [{}]", entityName, t.getId(), t.getTimestamp());
            return t;
        } catch (RuntimeException re) {
            LOG.debug("Unexpected error during creating entity [{}] ...", entityName, re);
            rollbackTransaction(entityTransaction);
            throw re;
        } finally {
            commitTransaction(entityTransaction);
        }
    }

    @Override
    public T read(final long id) {
        LOG.debug("Get entity [{}] with id [{}] ...", entityName, id);
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            beginTransaction(entityTransaction);
            final T t = entityManager.find(type, id);
            LOG.debug("Found entity [{}] with id [{}], timestamp [{}]", entityName, t.getId(), t.getTimestamp());
            return t;
        } catch (RuntimeException re) {
            LOG.debug("Unexpected error during reading entity [{}] ...", entityName, re);
            rollbackTransaction(entityTransaction);
            throw re;
        } finally {
            commitTransaction(entityTransaction);
        }
    }

    @Override
    public T update(final T t) {
        LOG.debug("Update entity [{}] with id [{}], timestamp [{}] ...", entityName, t.getId(), t.getTimestamp());
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            beginTransaction(entityTransaction);
            final T tUpdated = entityManager.merge(t);
            LOG.debug("Updated entity [{}] with id [{}], timestamp [{}]", entityName, t.getId(), t.getTimestamp());
            return tUpdated;
        } catch (RuntimeException re) {
            LOG.debug("Unexpected error during updating entity [{}] with id [{}], timestamp [{}] ...", entityName, t.getId(), t.getTimestamp(), re);
            rollbackTransaction(entityTransaction);
            throw re;
        } finally {
            commitTransaction(entityTransaction);
        }
    }

    @Override
    public void delete(final T t) {
        LOG.debug("Delete entity [{}] with id [{}], timestamp [{}] ...", entityName, t.getId(), t.getTimestamp());
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            beginTransaction(entityTransaction);
            entityManager.remove(entityManager.contains(t) ? t : entityManager.merge(t));
            LOG.debug("Deleted entity [{}] with id [{}], timestamp [{}]", entityName, t.getId(), t.getTimestamp());
        } catch (RuntimeException re) {
            LOG.debug("Unexpected error during deleting entity [{}] with id [{}], timestamp [{}] ...", entityName, t.getId(), t.getTimestamp(), re);
            rollbackTransaction(entityTransaction);
            throw re;
        } finally {
            commitTransaction(entityTransaction);
        }
    }

    public T getByCode(final String code) {
        LOG.debug("Get entity [{}] by code [{}] ...", entityName, code);
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            beginTransaction(entityTransaction);
            final Query query = entityManager.createNamedQuery(entityName + ".getByCode");
            query.setParameter("code", code);
            final T t = (T) query.getSingleResult();
            LOG.debug("Found entity [{}] with id [{}], timestamp [{}]", entityName, t.getId(), t.getTimestamp());
            return t;
        } catch (RuntimeException re) {
            LOG.debug("Unexpected error during reading entity [{}] by code [{}] ...", entityName, code, re);
            rollbackTransaction(entityTransaction);
            throw re;
        } finally {
            commitTransaction(entityTransaction);
        }
    }

    protected void beginTransaction(final EntityTransaction entityTransaction) {
        if (!entityTransaction.isActive()) {
            LOG.debug("Begin transaction [{}] ...", entityTransaction);
            entityTransaction.begin();
        }
    }

    protected void rollbackTransaction(final EntityTransaction entityTransaction) {
        if (entityTransaction.isActive()) {
            LOG.debug("Rollback transaction [{}] ...", entityTransaction);
            entityTransaction.rollback();
        }
    }

    protected void commitTransaction(final EntityTransaction entityTransaction) {
        if (entityTransaction.isActive()) {
            LOG.debug("Commit transaction [{}] ...", entityTransaction);
            entityTransaction.commit();
        }
    }
}
