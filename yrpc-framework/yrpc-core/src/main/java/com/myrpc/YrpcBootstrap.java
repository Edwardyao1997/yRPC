package com.myrpc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
     *
     * @param appName
     * @return 当前实例
     */
    public YrpcBootstrap application(String appName) {
        return this;
    }

    /**
     * 配置注册中心
     *
     * @param registryConfig 注册中心
     * @return
     */
    public YrpcBootstrap registry(RegistryConfig registryConfig) {
        return this;
    }

    /**
     * 配置协议
     *
     * @param protocolConfig
     * @return this
     */
    public YrpcBootstrap protocol(ProtocolConfig protocolConfig) {
        if(log.isDebugEnabled()){
            log.debug("此工程使用了"+protocolConfig.toString() + "协议进行序列化");
        }
        return this;
    }

    /**
     * 发布服务，注册到注册中心
     *
     * @param service 封装的需要发布的服务
     * @return
     */
    public YrpcBootstrap publish(ServiceConfig<?> service) {
        if(log.isDebugEnabled()){
            log.debug("服务{}，已被注册",service.getInterface().getName());
        }
        return this;
    }

    /**
     * 启动服务
     */
    public void start() {

    }

    /**
     * 调用方的API
     *
     * @param reference
     * @return
     */
    public YrpcBootstrap reference(ReferenceConfig<?> reference) {
        //拿到相关的配置项，对referrence进行配置，方便生成代理对象
        return this;
    }
}
