package com.myrpc;

import com.myrpc.discovery.Registry;
import com.myrpc.discovery.RegistryConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class YrpcBootstrap {
    // YrpcBootstrap是个单例，希望每个应用程序只有一个启动类
    private static final YrpcBootstrap yrpcBootstrap = new YrpcBootstrap();
    //定义一些基础配置
    private String applicationName = "default";
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    //todo:待处理
    private Registry registry;
    //维护已发布的且暴露的服务列表 key:接口全限定名称，value:serviceConfig
    private static final Map<String,ServiceConfig<?>> SERVICE_LIST = new HashMap<>(16);
    //连接缓存，一定要看一下key是否重写了equals和toString方法
    public static final Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>(16);
    private int port = 8088;
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
        //尝试获取一个注册中心
        this.registry = registryConfig.getRegistry();
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
        //抽象了注册中心的概念，使用注册中心的实现完成注册
        //
        registry.register(service);
        //1.服务调用方通过接口方法名和参数列表发起调用时，提供方如何知道使用哪个实现
        //（1）new (2)Spring获取 （3）自行维护映射关系
        SERVICE_LIST.put(service.getInterface().getName(),service);
        return this;
    }
    public YrpcBootstrap publish(List<ServiceConfig<?>> services) {
        //抽象了注册中心的概念，使用注册中心的实现完成注册
        for (ServiceConfig<?> service : services) {
            this.publish(service);
        }
        return this;
    }

    /**
     * 启动服务
     */
    public void start() {
        //使用Netty进行通信
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(10);
        try{
            //启动服务引导程序
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //配置服务器
            serverBootstrap = serverBootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel){
                            socketChannel.pipeline().addLast(null);
                        }
                    });
            //绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用方的API
     *
     * @param reference
     * @return
     */
    public YrpcBootstrap reference(ReferenceConfig<?> reference) {
        //拿到相关的配置项，对referrence进行配置，方便生成代理对象
        //reference需要注册中心
        reference.setRegistry(registry);
        return this;
    }
}
