package com.myrpc.discovery.impl;

import com.myrpc.Constant;
import com.myrpc.ServiceConfig;
import com.myrpc.discovery.AbstarctRegistry;
import com.myrpc.utils.Net.NetUtils;
import com.myrpc.utils.ZK.ZKNode;
import com.myrpc.utils.ZK.ZKUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;

@Slf4j
public class NacosRegistry extends AbstarctRegistry {
    private ZooKeeper zooKeeper;
    public NacosRegistry() {
        this.zooKeeper = ZKUtils.createZK();
    }
    public NacosRegistry(String connection, int timeout) {
        this.zooKeeper = ZKUtils.createZK(connection,timeout);
    }
    @Override
    public void register(ServiceConfig<?> service) {
        String parentNode = Constant.BASE_PROVIDERS+"/"+service.getInterface().getName();
        //该节点是持久连接
        if(!ZKUtils.exists(zooKeeper,parentNode,null)){
            ZKNode zkNode = new ZKNode(parentNode,null);
            ZKUtils.createNode(zooKeeper,zkNode,null, CreateMode.PERSISTENT);
        }
        //创建本机的临时节点ip:port
        //服务提供方的端口由自己来设定，还需要一个获取ip的方法
        //todo:全局保存端口的地方
        String node = parentNode+"/"+ NetUtils.getIP() + ":" + 8088;
        //创建临时节点
        if(!ZKUtils.exists(zooKeeper,node,null)){
            ZKNode zkNode = new ZKNode(node,null);
            ZKUtils.createNode(zooKeeper,zkNode,null, CreateMode.EPHEMERAL);
        }
        if(log.isDebugEnabled()){
            log.debug("服务{}，已被注册",service.getInterface().getName());
        }
    }

    @Override
    public InetSocketAddress lookFor(String serviceName) {
        return null;
    }
}
