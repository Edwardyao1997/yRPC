package com.myrpc.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

public interface Selector {
    /**
     * 根据服务列表执行均衡算法，获取节点
     * @param
     * @return 具体的节点
     */
    InetSocketAddress getNext();
    //todo 服务动态更新时需要进行rebalance
    void rebalance();
}
