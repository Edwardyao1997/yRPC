package com.myrpc.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务调用方发起的请求内容
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YrpcRequest {
    //请求的id
    private long requestId;
    //请求类型，压缩类型，序列化方法
    private byte requestType;
    private byte compressType;
    private byte serializeType;
    //消息体
    private RequestPayload requestPayload;
}
