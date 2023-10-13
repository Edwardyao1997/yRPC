package com.myrpc.discovery;

import com.myrpc.Constant;
import com.myrpc.Exceptions.DiscoveryException;
import com.myrpc.discovery.Registry;
import com.myrpc.discovery.impl.NacosRegistry;
import com.myrpc.discovery.impl.ZookeeperRegistry;

public class RegistryConfig {
    //定义连接的url: zookeeper://127.0.0.1
    private final String connection;

    public RegistryConfig(String connection) {
        this.connection = connection;
    }
    //简单工厂模式，来完成

    /**
     *
     * @return 返回具体的注册中心实例
     */
    public Registry getRegistry() {
        //获取注册中心的类型
        String registryType = getRegistryType(connection,true).toLowerCase().trim();
        if(registryType.equals("zookeeper")){
            String host = getRegistryType(connection, false);
            return new ZookeeperRegistry(host, Constant.TIME_OUT);
        } else if (registryType.equals("nacos")) {
            String host = getRegistryType(connection, false);
            return new NacosRegistry(host, Constant.TIME_OUT);
        }
        throw new DiscoveryException("Fail to find Registry");

    }
    private String getRegistryType(String connection,boolean isType){
        String[] typeAndHost= connection.split("://");
        if(typeAndHost.length != 2){
            throw new RuntimeException("注册中心连接地址不合法");
        }
        if(isType) {
            return typeAndHost[0];
        }
        else{
            return typeAndHost[1];
        }
    }
}
