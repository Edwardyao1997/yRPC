package com.myrpc.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 请求负载的封装类
 */
public class RequestPayload implements Serializable {
    //接口或者服务的名字
    private String interfaceName;
    //方法的名字
    private String methodName;
    //参数列表，分为参数类型和具体参数
    //参数类型用来找到重载方法，具体参数用来执行方法
    private Class<?>[] paraType;
    private Object[] paraValue;
    //返回值封装
    private Class<?> returnType;
}
