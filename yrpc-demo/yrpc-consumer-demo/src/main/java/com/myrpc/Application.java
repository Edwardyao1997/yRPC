package com.myrpc;

public class Application {
    public static void main(String[] args) {
        //reference中包含生成代理的模板方法
        ReferenceConfig<HelloYrpc> reference = new ReferenceConfig<>();
        reference.setInterface(HelloYrpc.class);
        //代理做了些什么？
        //1.连接注册中心
        //2.拉去服务列表
        //3.选择服务并建立连接
        //4.发送请求，携带调用信息，获得结果
        YrpcBootstrap.getInstance()
                .application("first-application-consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .reference(reference);
        //获取一个代理对象
        HelloYrpc helloYrpc = xxx.get();
        helloYrpc.sayhi("您好")
    }
}
