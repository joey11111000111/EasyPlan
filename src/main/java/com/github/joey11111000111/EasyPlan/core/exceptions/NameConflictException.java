package com.github.joey11111000111.EasyPlan.core.exceptions;

import com.github.joey11111000111.EasyPlan.core.Controller;

/**
 * This exception is thrown when someone tries to apply a new service name, which is
 * already an applied name of another bus service.<br>
 * See {@link Controller#applyChanges()} for more information.
 */
public class NameConflictException extends Exception {

    /**
     * Creates a new instance with the given error message.
     * @param message the error message
     */
    public NameConflictException(String message) {
        super(message);
    }
}
