package com.github.joey11111000111.EasyPlan.dao;

/**
 * Thrown when there is no previously saved object or something goes wrong
 * during the reading process.
 */
public class ObjectReadFailureException extends Exception {

    /**
     * Creates a new instance with the given error message.
     * @param message the error message of the exception
     */
    public ObjectReadFailureException(String message) {
        super(message);
    }
}
