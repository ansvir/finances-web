package org.tohant.financesweb.service;

import java.util.List;

public interface IService<T, ID> {

    List<T> findAll();

    T save(T entity);

    void updateAll(List<T> entities);

    long count();
}
