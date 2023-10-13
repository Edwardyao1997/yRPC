package com.myrpc;

import com.myrpc.Exceptions.NetworkException;
import com.myrpc.discovery.NettyBootstrapInitializer;
import com.myrpc.discovery.Registry;
import com.myrpc.discovery.RegistryConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

@Slf4j
public class ReferenceConfig<T> {
    private Class<T> interfaceRef;
    private Registry registry;

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Class<T> getInterface() {
        return interfaceRef;
    }

    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    /**
     * 代理设计模式，生成接口的代理对象
     * @return 代理对象
     */
    public T get() {
        //使用动态代理完成
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class[] classes = new Class[]{interfaceRef};
        //使用动态代理生成代理对象
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //
                log.info("method-->{}",method.getName());
                log.info("args-->{}",args);
                //1.发现服务，从注册中心，找一个可用的服务；
                //传入服务名字，返回ip+端口
                //todo:每次调用方法时候都需要注册中心拉取服务列表吗？ 使用本地缓存+Watcher
                //     如何合理的选择一个可用的服务，而不是只获取第一个? 实现负载均衡策略
                InetSocketAddress address = registry.lookFor(interfaceRef.getName());
                if(log.isDebugEnabled()){
                    log.debug("服务调用方发现了服务【{}】的可用主机【{}】",interfaceRef.getName(),address);
                }
                //2.使用netty连接服务器，发送请求
                //由于Netty是长链接，因此不应该每次都建立一个连接
                //解决方法：缓存channel,尝试从缓存中获取channel,获取不到再创建链接再缓存
                //从全局缓存中获取一个通道
                Channel channel = YrpcBootstrap.CHANNEL_CACHE.get(address);
                if(channel == null){
                    //sync和await都会阻塞当前线程，获取返回值，因为连接的过程是异步的，发送数据的过程也是异步的
                    //sync会主动在主线程抛出异常，await的异常在子线程中处理，需要使用future来处理
                    channel = NettyBootstrapInitializer.getBootstrap()
                            .connect(address).await().channel();
                    YrpcBootstrap.CHANNEL_CACHE.put(address,channel);
                }
                if(channel == null){
                    throw new NetworkException("获取channel发生异常");
                }
//                ChannelFuture channelFuture = channel.writeAndFlush(new Object()).await();
//                if(channelFuture.isDone()){
//                    Object object = channelFuture.getNow();
//                } else if (!channelFuture.isSuccess()) {
//                    //发生问题则要捕获异常,可以捕获异步任务中的异常
//                    Throwable cause = channelFuture.cause();
//                    throw new RuntimeException(cause)；
//                }
                //todo:异步策略
                channel.writeAndFlush(new Object()).addListener((ChannelFutureListener) promise ->{

                });

                return null;
            }
        });
        return (T)helloProxy;
    }
}
