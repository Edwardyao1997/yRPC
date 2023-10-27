package com.myrpc.serialize;

import com.myrpc.transport.message.RequestPayload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerializeUtil {
    public static byte[] serialize(Object object) {
        //todo 对不同的请求做出不同的处理
        //将对象编程序列化为一个字节数组
        if (object == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            //log.error("序列化时出现异常");
            throw new RuntimeException(e);
        }
    }
}
