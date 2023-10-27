package com.myrpc.proxy.handler;

import com.myrpc.Exceptions.DiscoveryException;
import com.myrpc.Exceptions.NetworkException;
import com.myrpc.YrpcBootstrap;
import com.myrpc.discovery.NettyBootstrapInitializer;
import com.myrpc.discovery.Registry;
import com.myrpc.enumration.RequestType;
import com.myrpc.serialize.SerializerFactory;
import com.myrpc.transport.message.RequestPayload;
import com.myrpc.transport.message.YrpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
/**
 * 封装了客户端通信的基础逻辑，每一个代理对象的远程调用过程，都封装在了invoke方法中
 * 1.发现可用服务
 * 2.建立连接
 * 3.发送请求
 * 4.得到结果
 */
public class RpcConsumerInvocationHandler implements InvocationHandler {
    //注册中心
    private final Registry registry;
    //代理的接口
    private final Class<?> interfaceRef;

    public RpcConsumerInvocationHandler(Registry registry, Class<?> interfaceRef) {
        this.registry = registry;
        this.interfaceRef = interfaceRef;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("method-->{}",method.getName());
        log.info("args-->{}",args);
        //1.发现服务，从注册中心，找一个可用的服务；
        //传入服务名字，返回ip+端口
        //如何合理的选择一个可用的服务，而不是只获取第一个? 实现负载均衡策略
        InetSocketAddress address = YrpcBootstrap.LOAD_BALANCER.selectServiceAddress(interfaceRef.getName());
        if(log.isDebugEnabled()){
            log.debug("服务调用方发现了服务【{}】的可用主机【{}】",interfaceRef.getName(),address);
        }
        //2.使用netty连接服务器，发送请求
        //由于Netty是长链接，因此不应该每次都建立一个连接
        Channel channel = getAvailableChannel(address);
        if(log.isDebugEnabled()){
            log.debug("获取了和【{}】建立的通道，准备发送数据",address);
        }
        /*
         * ---------------------报文封装----------------------
         */
        //构建payload
        RequestPayload requestPayload = RequestPayload.builder()
                .interfaceName(interfaceRef.getName())
                .methodName(method.getName())
                .paraType(method.getParameterTypes())
                .paraValue(args)
                .returnType(method.getReturnType())
                .build();
        //todo 抽象不同的请求类型和编码形式
        //构建请求体
        YrpcRequest yrpcRequest = YrpcRequest.builder()
                .requestId(YrpcBootstrap.ID_GENERATOR.getId())
                .compressType((byte) 1)
                .requestType(RequestType.REQUEST.getId())
                .serializeType(SerializerFactory.getSerializer(YrpcBootstrap.SERIALIZE_TYPE).getCode())
                .requestPayload(requestPayload)
                .build();
//                ChannelFuture channelFuture = channel.writeAndFlush(new Object()).await();
//                if(channelFuture.isDone()){
//                    Object object = channelFuture.getNow();
//                } else if (!channelFuture.isSuccess()) {
//                    //发生问题则要捕获异常,可以捕获异步任务中的异常
//                    Throwable cause = channelFuture.cause();
//                    throw new RuntimeException(cause)；
//                }
        //todo:异步策略
        //4.写出报文
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        //todo:将compeleteFuture暴露
        YrpcBootstrap.PENDING_REQUEST.put(yrpcRequest.getRequestId(), completableFuture);
        //这里写出了一个请求，这个请求的实例会进入pipeline,进而执行操作
        //第一个出站程序一定是将请求对象转化为二进制报文
        channel.writeAndFlush(yrpcRequest).addListener((ChannelFutureListener) promise ->{
            //当前promise的返回结果是writeAndFlush的返回结果
            //一旦数据被写出，这个promise就结束了
            //但是我们想要的是服务端给的返回值，这里不应处理completableFuture。只需要处理异常
            //只需要将completableFuture挂起并暴露，得到响应的时候调用compelete方法
//                    if(promise.isDone()){
//                        completableFuture.complete(promise.getNow());
            if(!promise.isSuccess()){
                completableFuture.completeExceptionally(promise.cause());
            }
        });
        //completableFuture.get(3,TimeUnit.SECONDS);
        //没有地方处理就会阻塞，等待complete执行
        //需要在pipeline中
        //5.获得响应的结果并返回
        return completableFuture.get(10,TimeUnit.SECONDS);
    }

    /**
     * 根据地质获取一个可用的channel
     * @param address
     * @return
     */
    private Channel getAvailableChannel(InetSocketAddress address) {
        //尝试从缓存中获取
        Channel channel = YrpcBootstrap.CHANNEL_CACHE.get(address);
        //没拿到就建立连接
        if(channel == null){
            //sync和await都会阻塞当前线程，获取返回值，因为连接的过程是异步的，发送数据的过程也是异步的
            //sync会主动在主线程抛出异常，await的异常在子线程中处理，需要使用future来处理
//                    channel = NettyBootstrapInitializer.getBootstrap()
//                            .connect(address).await().channel();
            CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
            NettyBootstrapInitializer.getBootstrap().connect(address).addListener(
                    (ChannelFutureListener) promise ->{
                        if(promise.isDone()){
                            //异步的，完成
                            if(log.isDebugEnabled()){
                                log.debug("已经和：【{}】成功建立了连接",address);
                            }
                            channelFuture.complete(promise.channel());
                        }else if(!promise.isSuccess()){
                            channelFuture.completeExceptionally(promise.cause());
                        }
                    });
            //阻塞获取
            try {
                channel = channelFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("获取通道时，发生异常",e);
                throw new DiscoveryException(e);
            }
            //缓存Channel
            YrpcBootstrap.CHANNEL_CACHE.put(address,channel);
        }
        if(channel == null){
            log.error("获取或建立与【{}】建立通道时发生异常",address);
            throw new NetworkException("获取channel发生异常");
        }
        return channel;
    }
}
