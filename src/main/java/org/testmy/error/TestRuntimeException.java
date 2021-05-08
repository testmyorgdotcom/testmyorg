package org.testmy.error;

public class TestRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 5764711734211534884L;

    public TestRuntimeException(final Throwable t) {
        super(String.format("Failed due to checked Exception: %s", t.getClass().getSimpleName()));
    }

    public TestRuntimeException(final String errorMessage) {
        super(errorMessage);
    }
}
