package com.myrpc.discovery.channelHandler;

import com.myrpc.proxy.handler.YrpcMessageEncodreHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //加入Netty的日志处理器
        socketChannel.pipeline()
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                .addLast(new YrpcMessageEncodreHandler())
                .addLast(new MySimpleChannelInboundHandler());
    }
}
