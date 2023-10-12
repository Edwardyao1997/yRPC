package com.myrpc;

public interface HelloYrpc {
    /**
     * 通用接口，provider和consumer都需要依赖
     * @param msg
     * @return
     */
    String sayhi(String msg);
}
