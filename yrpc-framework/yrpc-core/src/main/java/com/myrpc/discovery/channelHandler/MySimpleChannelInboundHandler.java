package com.myrpc.discovery.channelHandler;

import com.myrpc.YrpcBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

public class MySimpleChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        String string = msg.toString(Charset.defaultCharset());
        //从全局的挂起请求中寻找匹配的待处理cf
        CompletableFuture<Object> completableFuture = YrpcBootstrap.PENDING_REQUEST.get(1L);
        completableFuture.complete(string);
        //log.info("msg->{}",msg.toString(Charset.defaultCharset()));
    }
}
