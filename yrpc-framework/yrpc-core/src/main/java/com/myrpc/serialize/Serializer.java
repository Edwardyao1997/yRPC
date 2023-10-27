package com.myrpc.serialize;

/**
 * 序列化器
 */
public interface Serializer {
    /**
     * 序列化方法
     * @param object
     * @return 字节数组
     */
    byte[] serilize(Object object);

    /**
     * 反序列化方法
     * @param bytes 字节数组
     * @param clazz
     * @return 反序列化对象
     * @param <T>
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
