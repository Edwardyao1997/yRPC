package com.myrpc.proxy.handler;

import com.myrpc.enumration.RequestType;
import com.myrpc.transport.message.MessageFormatConstant;
import com.myrpc.transport.message.RequestPayload;
import com.myrpc.transport.message.YrpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Slf4j
/**
 * 出站时第一个经过的处理器
 * 4bytes magic：yrpc.getBytes()
 * 1byte version(版本号：1)
 * 2bytes header length:首部长度
 * 4bytes full length:报文总长度（解决粘包拆包）
 * 1byte serialize
 * 1byte compress
 * 1byte requestType
 * 8bytes requestId
 *
 * body 不定长
 */
public class YrpcMessageEncodreHandler extends MessageToByteEncoder<YrpcRequest> {
    /**
     * 封装报文的方法
     * @param channelHandlerContext
     * @param yrpcRequest
     * @param byteBuf 报文实际存放的字节数组
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, YrpcRequest yrpcRequest, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(MessageFormatConstant.MAGIC);
        byteBuf.writeByte(MessageFormatConstant.VERSION);
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);
        byteBuf.writerIndex(byteBuf.writerIndex()+MessageFormatConstant.FULL_FIELD_LENGTH);
        //总长度咱不知道
        byteBuf.writeByte(yrpcRequest.getRequestType());
        byteBuf.writeByte(yrpcRequest.getCompressType());
        byteBuf.writeByte(yrpcRequest.getSerializeType());
        byteBuf.writeLong(yrpcRequest.getRequestId());
        //判断，心跳请求就不处理请求体
        byte[] body = getBodyBytes(yrpcRequest.getRequestPayload());
        if(body == null){
            byteBuf.writeBytes(body);
        }
        //写入请求负载
        int bodyLength = body == null? 0:body.length;
        //保存当前写指针
        int currentIndex = byteBuf.writerIndex();
        //移动写指针至header总长度位置
        byteBuf.writerIndex(MessageFormatConstant.MAGIC.length
                +MessageFormatConstant.VERSION_LENGTH
                +MessageFormatConstant.HEADER_FIELD_LENGTH);
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH+bodyLength);
        //将写指针归位
        byteBuf.writerIndex(currentIndex);

    }
    private byte[] getBodyBytes(RequestPayload requestPayload){
        //todo 对不同的请求做出不同的处理
        //将对象编程序列化为一个字节数组
        if(requestPayload == null){
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = null;
            outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(requestPayload);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("序列化时出现异常");
            throw new RuntimeException(e);
        }
    }
}
