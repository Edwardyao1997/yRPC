package com.myrpc.impl;

import com.myrpc.HelloYrpc;

public class HelloIYrpcImpl implements HelloYrpc {
    @Override
    public String sayhi(String msg) {
        return "Hi, consumer:" + msg;
    }
}
