package com.myrpc.discovery.channelHandler;

import com.myrpc.YrpcBootstrap;
import com.myrpc.transport.message.YrpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
@Slf4j
public class MySimpleChannelInboundHandler extends SimpleChannelInboundHandler<YrpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, YrpcResponse yrpcResponse) throws Exception {
        //服务提供方返回的结果
        Object returnValue = yrpcResponse.getBody();
        //从全局的挂起请求中寻找匹配的待处理cf
        CompletableFuture<Object> completableFuture = YrpcBootstrap.PENDING_REQUEST.get(1L);
        completableFuture.complete(returnValue);
        if(log.isDebugEnabled()){
            log.debug("已寻找到编号为【{}】的compeletableFuture，处理响应",yrpcResponse.getRequestId());
        }
        //log.info("msg->{}",msg.toString(Charset.defaultCharset()));
    }
}
