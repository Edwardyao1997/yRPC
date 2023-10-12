package com.myrpc;

import java.lang.module.ResolvedModule;
import java.util.logging.Handler;

public class YrpcBootstrap {
    // YrpcBootstrap是个单例，希望每个应用程序只有一个启动类
    private static YrpcBootstrap yrpcBootstrap = new YrpcBootstrap();

    private YrpcBootstrap() {
        //构造一些初始化的过程
    }

    public static YrpcBootstrap getInstance() {
        return yrpcBootstrap;
    }

    /**
     * 定义当前应用的名字
     * @param appName
     * @return 当前实例
     */
    public YrpcBootstrap application(String appName) {
        return this;
    }

    /**
     * 配置注册中心
     * @param registryConfig 注册中心
     * @return
     */
    public YrpcBootstrap registry(RegistryConfig registryConfig) {
        return this;
    }

    /**
     * 配置协议
     * @param protocolConfig
     * @return this
     */
    public YrpcBootstrap protocol(ProtocolConfig protocolConfig) {
        return this;
    }

    /**
     * 发布服务
     * @return this
     */
    public YrpcBootstrap publish() {
        return this;
    }

    /**
     * 启动服务
     */
    public void start(){

    }

    public YrpcBootstrap reference(ReferenceConfig<?> reference) {
        //拿到相关的配置项，对referrence进行配置，方便生成代理对象
        return this;
    }
}
