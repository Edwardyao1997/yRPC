package com.myrpc.proxy.handler;

import com.myrpc.YrpcBootstrap;
import com.myrpc.serialize.Serializer;
import com.myrpc.serialize.SerializerFactory;
import com.myrpc.transport.message.MessageFormatConstant;
import com.myrpc.transport.message.YrpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

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
public class YrpcRequestEncoderHandler extends MessageToByteEncoder<YrpcRequest> {
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
        //总长度暂不知道
        byteBuf.writeByte(yrpcRequest.getRequestType());
        byteBuf.writeByte(yrpcRequest.getCompressType());
        byteBuf.writeByte(yrpcRequest.getSerializeType());
        byteBuf.writeLong(yrpcRequest.getRequestId());
        byteBuf.writeLong(yrpcRequest.getTimeStamp());
        //判断，心跳请求就不处理请求体
        //序列化可以被提取为工具类
        byte[] body = null;
        if(yrpcRequest.getRequestPayload() !=null) {
            Serializer serializer = SerializerFactory.getSerializer(YrpcBootstrap.SERIALIZE_TYPE).getSerializer();
            body = serializer.serilize(yrpcRequest.getRequestPayload());
        }
        if (body != null) {
            byteBuf.writeBytes(body);
        }
        int bodyLength = body == null ? 0 : body.length;

        // 重新处理报文的总长度
        // 先保存当前的写指针的位置
        int writerIndex = byteBuf.writerIndex();
        // 将写指针的位置移动到总长度的位置上
        byteBuf.writerIndex(MessageFormatConstant.MAGIC.length
                + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.HEADER_FIELD_LENGTH
        );
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + bodyLength);
        // 将写指针归位
        byteBuf.writerIndex(writerIndex);
        if(log.isDebugEnabled()){
            log.debug("请求【{}】已完成报文编码",yrpcRequest.getRequestId());
        }
    }
}
