package com.myrpc.heartbeat;

import com.myrpc.YrpcBootstrap;
import com.myrpc.discovery.NettyBootstrapInitializer;
import com.myrpc.discovery.Registry;
import com.myrpc.enumration.RequestType;
import com.myrpc.serialize.SerializerFactory;
import com.myrpc.transport.message.YrpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

@Slf4j
public class HeartBeatDetector {
    public static void detectHeartbeat(String serviceName){
        //从注册中心拉取服务列表并连接
        Registry registry = YrpcBootstrap.getInstance().getRegistry();
        List<InetSocketAddress> addresses = registry.lookFor(serviceName);
        //把连接缓存
        for(InetSocketAddress address : addresses) {
            try {
                if(YrpcBootstrap.CHANNEL_CACHE.containsKey(address)) {
                    Channel channel = NettyBootstrapInitializer.getBootstrap().connect(address).sync().channel();
                    YrpcBootstrap.CHANNEL_CACHE.put(address,channel);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //定时任务，发送消息
        //TODO 创建单线程的线程池并提交任务
        ExecutorService heartbeatThreadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread((r));
                thread.setDaemon(true);
                return thread;
            }
        });
        Thread heartbeatThread = new Thread(()->
                new Timer().scheduleAtFixedRate(new HeartBeatTask(),2000,2000),
                "yrpc-Heartbeat-Thread");
        heartbeatThreadPool.execute(heartbeatThread);
    }
    private static class HeartBeatTask extends TimerTask{

        @Override
        public void run() {
            log.debug("由线程【{}】执行心跳检测",Thread.currentThread());
            //遍历所有channel
            Map<InetSocketAddress,Channel> cache = YrpcBootstrap.CHANNEL_CACHE;
            for(Map.Entry<InetSocketAddress,Channel> entry : cache.entrySet()){
                Channel channel = entry.getValue();
                long start = System.currentTimeMillis();
                //构建一个心跳请求
                YrpcRequest yrpcRequest = YrpcRequest.builder()
                        .requestId(YrpcBootstrap.ID_GENERATOR.getId())
                        .compressType((byte) 1)
                        .requestType(RequestType.HEART_BEAT.getId())
                        .serializeType(SerializerFactory.getSerializer(YrpcBootstrap.SERIALIZE_TYPE).getCode())
                        .timeStamp(start)
                        .build();
                //写出报文
                CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                YrpcBootstrap.PENDING_REQUEST.put(yrpcRequest.getRequestId(), completableFuture);
                channel.writeAndFlush(yrpcRequest).addListener((ChannelFutureListener) promise ->{
                    if(!promise.isSuccess()){
                        completableFuture.completeExceptionally(promise.cause());
                    }
                });
                Long endTime = 0L;
                try {
                    completableFuture.get();
                    endTime = System.currentTimeMillis();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                Long time = endTime - start;
                log.debug("和【{}】服务器的响应时间是【{}】",entry.getKey(),time);
            }
        }
    }
}
