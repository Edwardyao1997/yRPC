package com.myrpc.loadbalance.impl;

import com.myrpc.loadbalance.Selector;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsistentHashSelector implements Selector {

    //hash环，用来存储服务器节点
    private SortedMap<Integer,InetSocketAddress> circle = new TreeMap<>();
    //虚拟节点个数
    private int virtualNodes;

    @Override
    public InetSocketAddress getNext() {
        return null;
    }

    @Override
    public void rebalance() {

    }
}
