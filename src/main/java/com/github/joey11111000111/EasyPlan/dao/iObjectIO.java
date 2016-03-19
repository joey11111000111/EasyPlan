package com.github.joey11111000111.EasyPlan.dao;

import com.github.joey11111000111.EasyPlan.core.exceptions.ObjectReadFailureException;

import java.io.Serializable;
import java.util.List;

/**
 * Created by joey on 3/18/16.
 */
public interface iObjectIO {

    void saveObjects(Serializable[] objects);
    <E> List<E> readObjects(Class<E> clazz) throws ObjectReadFailureException;


}//interface
