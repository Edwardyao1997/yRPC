package com.myrpc.discovery.channelHandler;

import com.myrpc.ServiceConfig;
import com.myrpc.YrpcBootstrap;
import com.myrpc.enumration.RequestType;
import com.myrpc.enumration.ResponseCode;
import com.myrpc.transport.message.RequestPayload;
import com.myrpc.transport.message.YrpcRequest;
import com.myrpc.transport.message.YrpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;

@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler <YrpcRequest>{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, YrpcRequest yrpcRequest) throws Exception {
        Channel channel = channelHandlerContext.channel();
        SocketAddress socketAddress = channel.remoteAddress();
        //限流则应该根据地质匹配限流器

        //获取负载内容
        RequestPayload requestPayload = yrpcRequest.getRequestPayload();
        //根据负载内容进行方法调用
        Object result = null;
        if(!(yrpcRequest.getRequestType() == RequestType.HEART_BEAT.getId())){
            result = callTargetMethod(requestPayload);
            if(log.isDebugEnabled()) {
                log.debug("请求【{}】已经在服务端完成方法调用", yrpcRequest.getRequestId());
        }
        }
        //封装响应
        YrpcResponse yrpcResponse = new YrpcResponse();
        yrpcResponse.setCode(ResponseCode.SUCCESS.getCode());
        yrpcResponse.setRequestId(yrpcRequest.getRequestId());
        yrpcResponse.setCompressType(yrpcRequest.getCompressType());
        yrpcResponse.setSerializeType(yrpcRequest.getSerializeType());
        yrpcResponse.setBody(result);
        //响应写出
        channelHandlerContext.channel().writeAndFlush(yrpcResponse);
    }

    private Object callTargetMethod(RequestPayload requestPayload) {
        String interfaceName = requestPayload.getInterfaceName();
        String methodName = requestPayload.getMethodName();
        Class<?>[] paraType = requestPayload.getParaType();
        Object[] paraValue = requestPayload.getParaValue();
        //寻找到匹配的具体的实现
        ServiceConfig<?> serviceConfig = YrpcBootstrap.SERVICE_LIST.get(interfaceName);
        Object refImpl = serviceConfig.getRef();
        Object returnValue = null;
        //反射调用 1.获取方法对象 2. 执行invoke方法
        try {
            Class<?> implClass = refImpl.getClass();
            Method method = implClass.getMethod(methodName, paraType);
            returnValue = method.invoke(refImpl, paraValue);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            log.error("调用服务【{}】的方法【{}】时发生了异常",interfaceName,methodName,e);
            throw new RuntimeException(e);
        }
        return returnValue;
    }
}
