package com.myrpc.loadbalance;

import com.myrpc.YrpcBootstrap;
import com.myrpc.discovery.Registry;
import com.myrpc.loadbalance.impl.RoundRobinLoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractLoadBalencer implements LoadBalancer{
    //一个服务匹配一个selector
    private Map<String,Selector> cache = new ConcurrentHashMap<>(8);

    @Override
    public InetSocketAddress selectServiceAddress(String serviceName) {
        //从cache中获取选择器
        Selector selector = cache.get(serviceName);
        //没有selector就新建
        if(selector == null){
            List<InetSocketAddress> serviceList = YrpcBootstrap.getInstance().getRegistry().lookFor(serviceName);
            selector = getSelector(serviceList);
            //将selector放入缓存中
            cache.put(serviceName,selector);
        }
        //获取可用节点
        return selector.getNext();
    }

    /**
     * 模板方法模式，该方法由子类进行扩展
     * @param serviceList
     * @return
     */
    public abstract Selector getSelector(List<InetSocketAddress> serviceList);
}
