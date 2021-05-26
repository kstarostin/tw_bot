package com.chatbot.dao;

public interface CommonDAO<T> {
    T create(T t);
    T read(long id);
    T update(T t);
    void delete(T t);
}
