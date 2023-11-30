package com.myrpc.protection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求熔断器
 * 三种状态：open close half-open
 */
public class LinkBreaker {
    //收集异常的数量和比例，决定是否开启
    private volatile boolean isOpen = false;
    //处理异常指标
    private AtomicInteger requestCount = new AtomicInteger(0);
    //异常请求数
    private AtomicInteger errorCount = new AtomicInteger(0);
    //熔断门限
    private int breakThreshold;
    //异常比率
    private float errorRate;

    public LinkBreaker(int breakThreshold, float errorRate) {
        this.breakThreshold = breakThreshold;
        this.errorRate = errorRate;
    }
    //核心方法，判断是否开启熔断
    public boolean isBreak(){
        //已打开就直接返回
        if(isOpen){
            return true;
        }
        //对数据指标做判断，是否满足当前阈值
        //错误请求数量
        if(errorCount.get()>breakThreshold){
            this.isOpen = true;
            return true;
        }
        //错误比率
        if(errorCount.get()>0 && requestCount.get()>0
                && (float)(errorCount.get()/requestCount.get())>errorRate){
            this.isOpen = true;
            return true;
        }
        return false;
    }
    //统计方法，对指标完成计数
    public void requestRecorder(){
        this.requestCount.getAndIncrement();
    }
    public void errorRecorder(){
        this.errorCount.getAndIncrement();
    }

    /**
     * 重置熔断器
     */
    public void reset(){
        this.isOpen = false;
        this.requestCount.set(0);
        this.errorCount.set(0);
    }
}
