package com.github.joey11111000111.EasyPlan.core.exceptions;

/**
 * This exception is thrown when someone tries to make a modification on the selected service,
 * while there is no selected service.
 * It is a derivation of {@link RuntimeException} because with normal behaviour this is avoided.
 * Also quite a lot of methods in the {@link com.github.joey11111000111.EasyPlan.core.Controller Controller}
 * interface can throw this exception, always catching them would make the code verbose and less readable.
 */
public class NoSelectedServiceException extends RuntimeException {

    /**
     * The only constructor, creates a new instance with no error message.
     */
    public NoSelectedServiceException() {
    }
}
