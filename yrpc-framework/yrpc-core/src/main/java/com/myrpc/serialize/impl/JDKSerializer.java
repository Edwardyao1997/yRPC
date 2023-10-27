package com.myrpc.serialize.impl;

import com.myrpc.Exceptions.SerializeException;
import com.myrpc.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class JDKSerializer implements Serializer {
    @Override
    public byte[] serilize(Object object) {
        if (object == null) {
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(baos)
        )
        {
            outputStream.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("序列化对象【{}】时出现异常",object);
            throw new SerializeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if(bytes == null || clazz == null){
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream inputStream = new ObjectInputStream(bais)
        )
        {
            Object object = inputStream.readObject();
            if(log.isDebugEnabled()){
                log.debug("对象【{}】已经完成了反序列化操作.",clazz);
            }
            return (T)object;
        } catch (IOException | ClassNotFoundException e) {
            log.error("反序列化对象【{}】时出现异常",clazz);
            throw new SerializeException(e);
        }
    }
}
