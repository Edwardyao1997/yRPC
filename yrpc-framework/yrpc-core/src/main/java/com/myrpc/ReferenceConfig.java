package com.myrpc;

import com.myrpc.discovery.Registry;
import com.myrpc.discovery.RegistryConfig;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

@Slf4j
public class ReferenceConfig<T> {
    private Class<T> interfaceRef;
    private Registry registry;

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Class<T> getInterface() {
        return interfaceRef;
    }

    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    /**
     * 代理设计模式，生成接口的代理对象
     * @return 代理对象
     */
    public T get() {
        //使用动态代理完成
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class[] classes = new Class[]{interfaceRef};
        //使用动态代理生成代理对象
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //
                log.info("method-->{}",method.getName());
                log.info("args-->{}",args);
                //1.发现服务，从注册中心，找一个可用的服务；
                //传入服务名字，返回ip+端口
                //todo:每次调用方法时候都需要注册中心拉取服务列表吗？ 使用本地缓存+Watcher
                //     如何合理的选择一个可用的服务，而不是只获取第一个? 实现负载均衡策略
                InetSocketAddress address = registry.lookFor(interfaceRef.getName());
                if(log.isDebugEnabled()){
                    log.debug("服务调用方发现了服务【{}】的可用主机【{}】",interfaceRef.getName(),address);
                }
                //2.使用netty连接服务器，发送请求
                return null;
            }
        });
        return (T)helloProxy;
    }
}
