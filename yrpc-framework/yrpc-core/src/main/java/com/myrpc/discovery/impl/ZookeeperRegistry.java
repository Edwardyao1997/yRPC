package com.myrpc.discovery.impl;

import com.myrpc.Constant;
import com.myrpc.Exceptions.DiscoveryException;
import com.myrpc.ServiceConfig;
import com.myrpc.discovery.AbstarctRegistry;
import com.myrpc.utils.Net.NetUtils;
import com.myrpc.utils.ZK.ZKNode;
import com.myrpc.utils.ZK.ZKUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZookeeperRegistry extends AbstarctRegistry {
    private ZooKeeper zooKeeper;
    public ZookeeperRegistry() {
        this.zooKeeper = ZKUtils.createZK();
    }
    public ZookeeperRegistry(String connection,int timeout) {
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

    /**
     * 注册中心的应该返回可用的服务列表
     *
     * @param serviceName 服务名称
     * @return 服务列表
     */
    @Override
    public List<InetSocketAddress> lookFor(String serviceName) {
        // 找到服务对应节点
        String serviceNode = Constant.BASE_PROVIDERS+"/"+serviceName;
        // 从zk中获取子节点
        List<String> children = ZKUtils.getChildren(zooKeeper,serviceNode,null);
        // 获取所有可用的服务列表
        List<InetSocketAddress> inetSocketAddresses = children.stream().map(ipString -> {
            String[] ipAndPort = ipString.split(":");
            String ip = ipAndPort[0];
            int port = Integer.valueOf(ipAndPort[1]);
            return new InetSocketAddress(ip, port);
        }).toList();
        if(inetSocketAddresses.size() == 0){
            throw new DiscoveryException("未发现任何服务主机");
        }
        return inetSocketAddresses;
    }
}
