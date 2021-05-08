package org.testmy.error;

public class AbilityIsAbsentException extends RuntimeException {
    private static final long serialVersionUID = -5829232300346156421L;

    public AbilityIsAbsentException(String message) {
        super(message);
    }
}
