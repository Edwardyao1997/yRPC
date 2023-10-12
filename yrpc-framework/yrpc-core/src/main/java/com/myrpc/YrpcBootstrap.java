package com.myrpc;

import com.myrpc.utils.ZK.ZKNode;
import com.myrpc.utils.ZK.ZKUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

@Slf4j
public class YrpcBootstrap {
    // YrpcBootstrap是个单例，希望每个应用程序只有一个启动类
    private static final YrpcBootstrap yrpcBootstrap = new YrpcBootstrap();
    //定义一些基础配置
    private String applicationName = "default";
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private ZooKeeper zooKeeper;
    private YrpcBootstrap() {
        //构造一些初始化的过程
    }

    public static YrpcBootstrap getInstance() {
        return yrpcBootstrap;
    }

    /**
     * 定义当前应用的名字
     *
     * @param appName
     * @return 当前实例
     */
    public YrpcBootstrap application(String appName) {
        this.applicationName = appName;
        return this;
    }

    /**
     * 配置注册中心
     *
     * @param registryConfig 注册中心
     * @return
     */
    public YrpcBootstrap registry(RegistryConfig registryConfig) {
        //维护一个zk实例，但是会造成耦合；
        zooKeeper = ZKUtil.createZK();
        this.registryConfig = registryConfig;
        return this;
    }

    /**
     * 配置协议
     *
     * @param protocolConfig
     * @return this
     */
    public YrpcBootstrap protocol(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
        if(log.isDebugEnabled()){
            log.debug("此工程使用了"+protocolConfig.toString() + "协议进行序列化");
        }
        return this;
    }

    /**
     * 发布服务，注册到注册中心
     *
     * @param service 封装的需要发布的服务
     * @return
     */
    public YrpcBootstrap publish(ServiceConfig<?> service) {
        //服务节点名称
        String parentNode = Constant.BASE_PROVIDERS+"/"+service.getInterface().getName();
        //该节点是持久连接
        if(!ZKUtil.exists(zooKeeper,parentNode,null)){
            ZKNode zkNode = new ZKNode(parentNode,null);
            ZKUtil.createNode(zooKeeper,zkNode,null, CreateMode.PERSISTENT);
        }
        //创建本机的临时节点

        if(log.isDebugEnabled()){
            log.debug("服务{}，已被注册",service.getInterface().getName());
        }
        return this;
    }

    /**
     * 启动服务
     */
    public void start() {

    }

    /**
     * 调用方的API
     *
     * @param reference
     * @return
     */
    public YrpcBootstrap reference(ReferenceConfig<?> reference) {
        //拿到相关的配置项，对referrence进行配置，方便生成代理对象
        return this;
    }
}
