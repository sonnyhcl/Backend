package supplychain.service;

import java.util.List;

public interface BaseService<T> {
    void save(T e);

    void delete(Long id);

    T findOne(Long id);

    void flush();

    <S extends T> S saveAndFlush(S e);

    List<T> findAll();

    void delete(T entity);
}
