package com.myrpc.Exceptions;

public class NetworkException extends RuntimeException{
    public NetworkException() {
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }
}
