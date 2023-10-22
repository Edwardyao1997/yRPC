package com.myrpc.proxy.handler;

import com.myrpc.YrpcBootstrap;
import com.myrpc.enumration.RequestType;
import com.myrpc.transport.message.MessageFormatConstant;
import com.myrpc.transport.message.RequestPayload;
import com.myrpc.transport.message.YrpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
@Slf4j
public class YrpcMessageDecoderHandler extends LengthFieldBasedFrameDecoder {
    public YrpcMessageDecoderHandler() {
        super(
                //最大帧长度，超过则直接丢弃
                MessageFormatConstant.MAX_FRAME_LENGTH,
                //长度字段的偏移量
                MessageFormatConstant.MAGIC.length + MessageFormatConstant.VERSION_LENGTH + 2,
                //长度字段的长度
                MessageFormatConstant.FULL_FIELD_LENGTH,
                //负载的适配长度
                -(MessageFormatConstant.MAGIC.length + MessageFormatConstant.VERSION_LENGTH + 2 + MessageFormatConstant.FULL_FIELD_LENGTH),
                0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if( decode instanceof ByteBuf byteBuf){
            return decodeFrame(byteBuf);
        }
        return null;
    }

    private Object decodeFrame(ByteBuf byteBuf) {
        //解析魔数
        byte[] magic = new byte[MessageFormatConstant.MAGIC.length];
        byteBuf.readBytes(magic);
        //匹配检测
        for (int i = 0; i < magic.length ; i++) {
            if(magic[i] != MessageFormatConstant.MAGIC[i]){
                throw new RuntimeException("收到了非法的请求");
            }

        }
        //解析版本号
        byte version = byteBuf.readByte();
        if(version > MessageFormatConstant.VERSION){
            throw new RuntimeException("请求版本不支持");
        }
        //解析首部长度
        short headLength = byteBuf.readShort();
        //解析总长度
        int fullLength = byteBuf.readInt();
        //请求类型
        byte requestTyte = byteBuf.readByte();
        //序列化类型
        byte serializeType = byteBuf.readByte();
        //压缩类型
        byte compressType = byteBuf.readByte();
        //请求id
        long requestId = byteBuf.readLong();
        //封装接收到的报文
        YrpcRequest yrpcRequest = new YrpcRequest();
        yrpcRequest.setRequestType(requestTyte);
        yrpcRequest.setCompressType(compressType);
        yrpcRequest.setSerializeType(serializeType);
        //todo 心跳检测的请求没有负载，可以执行判断并直接返回
        if(requestTyte == RequestType.HEART_BEAT.getId()){
            return yrpcRequest;
        }
        //读取字节数组
        int payloadLength = fullLength - headLength;
        byte[] payload = new byte[payloadLength];
        byteBuf.readBytes(payload);
        //有字节数组后可以进行解压缩，反序列化
        //todo 反序列化
        try (ByteArrayInputStream bis = new ByteArrayInputStream(payload);
             ObjectInputStream ois = new ObjectInputStream(bis);
             ){
            RequestPayload requestPayload = (RequestPayload) ois.readObject();
            yrpcRequest.setRequestPayload(requestPayload);
        } catch (IOException | ClassNotFoundException e) {
            log.error("请求【{}】解析时发生错误",requestId,e);
        }
        return yrpcRequest;
    }
}
