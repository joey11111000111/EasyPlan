package com.github.joey11111000111.EasyPlan.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Created by joey on 3/18/16.
 */
public interface iObjectIO {

    void saveObject(Object object, Class<?> clazz) throws ObjectSaveFailureException;
    <E> E readObject(Class<E> clazz) throws ObjectReadFailureException;


}//interface
