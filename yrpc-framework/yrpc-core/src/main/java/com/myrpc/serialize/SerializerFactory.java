package com.myrpc.serialize;

import com.myrpc.serialize.Serializer;
import com.myrpc.serialize.impl.JDKSerializer;

import java.util.concurrent.ConcurrentHashMap;

public class SerializerFactory {
    private final static ConcurrentHashMap<String,SerializerWrapper> SERIALIZER_CACHE = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Byte,SerializerWrapper> SERIALIZER_CODE_CACHE = new ConcurrentHashMap<>();
    static {
        SerializerWrapper jdk = new SerializerWrapper((byte) 1, "JDK", new JDKSerializer());
        SERIALIZER_CACHE.put("JDK",jdk);
        SERIALIZER_CODE_CACHE.put((byte)1,jdk);
    }

    /**
     *
     * @param serializeType
     * @return 包装类
     */
    public static SerializerWrapper getSerializer(String serializeType){
        return SERIALIZER_CACHE.get(serializeType);
    }
    public static SerializerWrapper getSerializer(byte serializeCode){
        return SERIALIZER_CODE_CACHE.get(serializeCode);
    }
}
