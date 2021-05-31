package com.chatbot.dao;

import com.chatbot.entity.AbstractEntity;

public interface CommonDAO<T extends AbstractEntity> {
    T create(T t);
    T read(long id);
    T update(T t);
    void delete(T t);
}
