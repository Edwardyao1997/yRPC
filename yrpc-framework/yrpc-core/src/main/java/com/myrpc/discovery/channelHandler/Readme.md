服务调用方

1.发送报文 writeAndFlush(object)【封装请求】


YrpcRequest (请求Id(long) 压缩类型(byte) 序列化方式(byte) 消息类型(普通请求，心跳检测)(byte) 负载 (接口名称，方法名字，方法参数列表，返回值类型,不定长) )


pipeline就生效，报文出站

处理器一(log)：日志记录入站出站均会打印

处理器二(out)：将object转化为请求对象(msg)->进行序列化->压缩为字节码




服务提供方

2.接收报文 writeAndFlush(object)
pipeline就生效，报文出站

处理器一(in/out)：log

处理器二(in)：解压缩，进行反序列化（msg->yrpcrequest）

处理器三(out)：解析报文

服务提供方

3.执行方法调用

4.服务提供方返回响应
pipeline就生效，报文出站

处理器一(out)：将object转化为请求对象(msg)

处理器二(out)：进行序列化

处理器三(out)：压缩为字节码

5.服务调用方接收响应报文

处理器一(in)：字节码解压缩

处理器二(in)：进行反序列化

处理器三(out)：解析报文

6.将结果返回