package com.myrpc.discovery;

import com.myrpc.ServiceConfig;

import java.net.InetSocketAddress;

/**
 * 抽象注册中心，一个注册中心应具备什么样的能力
 */
public interface Registry {
    /**
     * 服务注册
     * @param serviceConfig 服务配置项
     */
    void register(ServiceConfig<?> serviceConfig);

    /**
     * 从注册中心拉取一个可用服务
     * @param serviceName 服务名称
     * @return 服务ip+端口
     */
    InetSocketAddress lookFor(String serviceName);
}
