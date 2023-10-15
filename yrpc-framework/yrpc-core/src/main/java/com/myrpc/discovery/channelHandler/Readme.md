服务调用方

1.发送报文 writeAndFlush(object)【封装请求】
pipeline就生效，报文出站

处理器一(out)：将object转化为请求对象(msg)

处理器二(out)：进行序列化

处理器三(out)：压缩为字节码


服务提供方

2.接收报文 writeAndFlush(object)
pipeline就生效，报文出站

处理器一(in)：字节码解压缩

处理器二(in)：进行反序列化

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