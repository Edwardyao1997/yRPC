package com.myrpc.protection;

public interface RateLimter {
    boolean allowRequest();
}
