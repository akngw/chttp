package org.example.chttp.extension;

public class ExitException extends RuntimeException {
    private final int status;

    public ExitException(int status) {
        super("System.exit called");
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
