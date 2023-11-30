package com.myrpc.protection;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 基于令牌桶算法的限流器
 */
public class TokenBucketRateLimiter implements RateLimter{
    //令牌：
    //令牌桶：用int来表示，大于零则表明桶里存在令牌

    public TokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        tokens = 0;
    }
    /**
     * 令牌数量，大于零则说明有令牌
     */
    private int tokens;
    /**
     * 令牌桶容量
     */
    private int capacity;
    /**
     * 令牌桶中的令牌数量应该不断增加
     * 可以启动一个定时任务来实现，每秒加rate
     */
    private int rate;
    /**
     * 请求能否被放行
     * @return true:放行 false:拦截
     */
    public synchronized boolean allowRequest() {
        //定时任务给令牌桶添加令牌
        //此处逻辑应放在类外面执行


        //获取令牌:
        //有令牌则放行，无令牌则拦截
        if (tokens > 0) {
            tokens--;
            return true;
        } else {
            return false;
        }
    }


    public static void main(String[] args) {
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(100,100);
        ScheduledExecutorService tokenAdder = new ScheduledThreadPoolExecutor(1);
        tokenAdder.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                rateLimiter.tokens = Math.min(rateLimiter.capacity, rateLimiter.tokens+rateLimiter.rate);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        for(int i = 0;i<1000;i++){
            boolean allowRequest = rateLimiter.allowRequest();
            System.out.println("第"+i+"次请求结果" + allowRequest);
        }
    }
}
