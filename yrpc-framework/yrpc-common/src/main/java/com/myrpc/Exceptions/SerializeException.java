package com.myrpc.Exceptions;

public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }

    public SerializeException() {
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
