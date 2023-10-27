package com.myrpc.Exceptions;

public class LoadBalanceException extends RuntimeException {
    public LoadBalanceException(String message) {
        super(message);
    }

    public LoadBalanceException(){}
}
