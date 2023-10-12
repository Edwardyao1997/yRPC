package com.myrpc;

public class Constant {
    //默认链接地址
    public static final String DEFAULT_ZK_CONNECTION = "127.0.0.1:2181";
    //默认超时时间
    public static final int TIME_OUT = 10000;
    //提供方和调用方在ZK中的基础路径
    public static final String BASE_PROVIDERS = "/myrpc-metadata/providers";
    public static final String BASE_CONSUMERS = "/myrpc-metadata/comsumers";
}
