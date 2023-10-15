package com.myrpc.transport.message;

public class MessageFormatConstant {
    public final static byte[] MAGIC = "yrpc".getBytes();
    public final static byte VERSION = 1;
    public final static short HEADER_LENGTH = (byte)(MAGIC.length+18);
    public final static short FULL_LENGTH = 2;
}
