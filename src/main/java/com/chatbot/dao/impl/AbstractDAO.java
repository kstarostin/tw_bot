package com.chatbot.dao.impl;

import com.chatbot.dao.CommonDAO;
import com.chatbot.entitymanager.JpaEntityManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;

public class AbstractDAO<T> implements CommonDAO<T> {
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
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            if (!entityTransaction.isActive()) {
                entityTransaction.begin();
            }
            entityManager.persist(t);
            return t;
        } catch (RuntimeException re) {
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

    @Override
    public T read(final long id) {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            if (!entityTransaction.isActive()) {
                entityTransaction.begin();
            }
            return entityManager.find(type, id);
        } catch (RuntimeException re) {
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

    @Override
    public T update(final T t) {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            if (!entityTransaction.isActive()) {
                entityTransaction.begin();
            }
            return entityManager.merge(t);
        } catch (RuntimeException re) {
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

    @Override
    public void delete(final T t) {

        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            if (!entityTransaction.isActive()) {
                entityTransaction.begin();
            }
            entityManager.remove(entityManager.contains(t) ? t : entityManager.merge(t));
        } catch (RuntimeException re) {
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

    public T getByCode(final String code) {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            if (!entityTransaction.isActive()) {
                entityTransaction.begin();
            }
            final Query query = entityManager.createNamedQuery(entityName + ".getByCode");
            query.setParameter("code", code);
            return (T) query.getSingleResult();
        } catch (RuntimeException re) {
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
