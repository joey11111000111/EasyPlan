package com.github.joey11111000111.EasyPlan.dao;

/**
 * Thrown when something goes wrong during the object saving process.
 */
public class ObjectSaveFailureException extends Exception {

    /**
     * Creates a new instance with the given error message.
     * @param message the error message of the exception
     */
    public ObjectSaveFailureException(String message) {
        super(message);
    }
}
