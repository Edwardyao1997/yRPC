package com.myrpc.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * 对请求的响应
 */
public class YrpcResponse {
    /**
     * 对应的请求Id
     */
    private long requestId;
    /**
     * 响应码
     * 1:成功
     * 2：异常
     */
    private byte code;
    private byte compressType;
    private byte serializeType;
    /**
     * 响应消息体
     */
    private Object body;
}
