package com.chatbot.entitymanager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaEntityManager {
    private static final String PERSISTENCE_UNIT_NAME = "TW_BOT";

    private static EntityManagerFactory emf;
    private static EntityManager em;

    private JpaEntityManager() {
    }

    public static EntityManager getEntityManager() {
        if (em == null) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            em = emf.createEntityManager();
        }
        return em;
    }
}
