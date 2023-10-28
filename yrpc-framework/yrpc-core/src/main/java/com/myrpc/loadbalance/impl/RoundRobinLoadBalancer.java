package com.myrpc.loadbalance.impl;

import com.myrpc.Exceptions.LoadBalanceException;
import com.myrpc.YrpcBootstrap;
import com.myrpc.discovery.Registry;
import com.myrpc.loadbalance.AbstractLoadBalencer;
import com.myrpc.loadbalance.LoadBalancer;
import com.myrpc.loadbalance.Selector;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer extends AbstractLoadBalencer {

    @Override
    public Selector getSelector(List<InetSocketAddress> serviceList) {
        return new RoundRobinSelector(serviceList);
    }

    @Slf4j
    private static class RoundRobinSelector implements Selector{
        private List<InetSocketAddress> serviceList;
        private AtomicInteger index;

        public RoundRobinSelector(List<InetSocketAddress> serviceList) {
            this.serviceList = serviceList;
            this.index = new AtomicInteger(0);
        }

        @Override
        public InetSocketAddress getNext() {
            if(serviceList == null || serviceList.size() == 0){
                log.error("负载均衡时，服务列表为空");
                throw new LoadBalanceException();
            }
            InetSocketAddress address = serviceList.get(index.get());
            if(index.get() == serviceList.size()-1){
                index.set(0);
            }
            //结束后游标后移
            index.incrementAndGet();
            return address;
        }

        @Override
        public void rebalance() {

        }
    }
}
