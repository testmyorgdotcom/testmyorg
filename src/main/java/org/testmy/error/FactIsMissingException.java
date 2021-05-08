package org.testmy.error;

public class FactIsMissingException extends RuntimeException {
    private static final long serialVersionUID = -5829232300346156421L;

    public FactIsMissingException(String message) {
        super(message);
    }
}
