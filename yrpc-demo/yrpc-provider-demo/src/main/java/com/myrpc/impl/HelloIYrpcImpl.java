package com.myrpc.impl;

import com.myrpc.HelloYrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelloIYrpcImpl implements HelloYrpc {
    @Override
    public String sayhi(String msg) {
        return "Hi, consumer:" + msg;
    }
}
