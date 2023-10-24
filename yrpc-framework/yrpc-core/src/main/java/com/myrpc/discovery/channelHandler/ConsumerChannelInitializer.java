package com.myrpc.discovery.channelHandler;

import com.myrpc.proxy.handler.YrpcRequestEncoderHandler;
import com.myrpc.proxy.handler.YrpcResponseDecoderHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                .addLast(new YrpcRequestEncoderHandler())
                .addLast(new YrpcResponseDecoderHandler())
                .addLast(new MySimpleChannelInboundHandler());

    }
}
