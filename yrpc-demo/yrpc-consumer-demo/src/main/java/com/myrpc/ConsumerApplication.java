package com.myrpc;

import com.myrpc.discovery.RegistryConfig;
import com.myrpc.heartbeat.HeartBeatDetector;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ConsumerApplication {
    public static void main(String[] args) {
        //reference中包含生成代理的模板方法
        ReferenceConfig<HelloYrpc> reference = new ReferenceConfig<>();
        reference.setInterface(HelloYrpc.class);
        //代理做了些什么？
        //1.连接注册中心
        //2.拉取服务列表
        //3.选择服务并建立连接
        //4.发送请求，携带调用信息，获得结果
        YrpcBootstrap.getInstance()
                .application("first-application-consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .serialize("JDK")
                .reference(reference);
        //获取一个代理对象
        HelloYrpc helloYrpc = reference.get();
        String sayhi = helloYrpc.sayhi("您好");
        log.info("sayHi->{}",sayhi);
        log.info("开始心跳检测");
        HeartBeatDetector.detectHeartbeat(HelloYrpc.class.getName());
    }
}
