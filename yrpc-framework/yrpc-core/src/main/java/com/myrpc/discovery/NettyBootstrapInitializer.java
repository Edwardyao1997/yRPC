package com.myrpc.discovery;

import com.myrpc.Exceptions.NetworkException;
import com.myrpc.YrpcBootstrap;
import com.myrpc.discovery.channelHandler.ConsumerChannelInitializer;
import com.myrpc.discovery.channelHandler.MySimpleChannelInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 *提供bootStrap实例
 */
@Slf4j
public class NettyBootstrapInitializer {
    private static final Bootstrap bootstrap = new Bootstrap();

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ConsumerChannelInitializer());
    }
    private NettyBootstrapInitializer() {
    }

    public static Bootstrap getBootstrap() {
       return bootstrap;
    }
}
