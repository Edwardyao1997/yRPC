package com.myrpc.discovery.config;

import com.myrpc.protection.RateLimter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
    //为IP配置限流器
    private Map<InetSocketAddress, RateLimter> IP_RATE_LIMITER = new ConcurrentHashMap<>(16);
}
