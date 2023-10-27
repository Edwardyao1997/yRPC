package com.myrpc.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 负载均衡器的接口
 */
public interface LoadBalancer {
    //能力：根据服务名称，找到可用的服务列表
    InetSocketAddress selectServiceAddress(String serviceName);
}
