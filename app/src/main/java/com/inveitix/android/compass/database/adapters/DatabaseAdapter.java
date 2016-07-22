package com.inveitix.android.compass.database.adapters;

import java.util.List;

public interface DatabaseAdapter<T> {

    T findById(int id);

    List<T> findAll(String sortOrder);

    void insert(T model);

    void update(T model);

    void delete(int id);
}
