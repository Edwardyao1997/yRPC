package com.myrpc;

import com.myrpc.discovery.RegistryConfig;
import com.myrpc.impl.HelloIYrpcImpl;

public class ProviderApplication {
    public static void main(String[] args) {
        //服务提供方，注册并启动服务
        //1.封装要发布的服务
        ServiceConfig<HelloYrpc> service = new ServiceConfig<>();
        service.setInterface(HelloYrpc.class);
        service.setRef(new HelloIYrpcImpl());
        //2.定义注册中心
        //3.启动引导程序，启动服务提供方
        //3.1 配置 -- 服务的名称 -- 注册中心 --序列化协议 -- 压缩算法
        //3.2
        YrpcBootstrap.getInstance()
                .application("first-application-provider")
                //配置注册中心
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .protocol(new ProtocolConfig("jdk"))
                //服务发布
                .publish(service)
                //启动服务
                .start();
    }
}
