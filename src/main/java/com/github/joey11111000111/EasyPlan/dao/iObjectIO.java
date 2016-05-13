package com.github.joey11111000111.EasyPlan.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Handles the saving and loading of an object to a file.
 */
public interface iObjectIO {

    /**
     * Saves the given object of given class to a file.
     * @param object the object to save
     * @param clazz the {@link Class} of the given object
     * @throws ObjectSaveFailureException if any kind of error happens through the saving process
     */
    void saveObject(Object object, Class<?> clazz) throws ObjectSaveFailureException;

    /**
     * Reads the previously saved object from file.
     * @param clazz the {@link Class} of the previously saved object
     * @param <E> the type of the saved object
     * @return the previously saved object read from file
     * @throws ObjectReadFailureException if there is no previously saved object or
     *      something goes wrong through the reading process
     */
    <E> E readObject(Class<E> clazz) throws ObjectReadFailureException;


}//interface
