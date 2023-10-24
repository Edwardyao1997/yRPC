package com.myrpc.utils;

import java.util.concurrent.atomic.LongAdder;

/**
 * 分布式全局唯一ID生成器
 */
public class IdGenerator {
    // 雪花算法：
    // 数据中心编码：5bit（最多32个）
    // 机器号：     5bit(最多32个)
    // TimeStamp： 42bit(选择较近的日期作为基准，大概能表示60-70年的时间范围)
    // 并发量：     12bit(同一机房同一机器同一时刻不同服务的编号)

    // 时间戳
    public static final long TIME_BEGINING = DateUtil.get("2023-10-1").getTime();
    public static final int DATA_CENTER_BIT = 5;
    public static final int HOST_ID_BIT = 5;
    public static final int SEQUENCE_BIT = 12;

    // 最大值
    public static final long DATA_CENTER_MAX = ~(-1L << DATA_CENTER_BIT);
    public static final long HOST_ID_MAX = ~(-1L << HOST_ID_BIT);
    public static final long SEQUENCE_MAX = ~(-1L << SEQUENCE_BIT);

    public static final long TIME_STAMP_LEFT_BIAS = DATA_CENTER_BIT+HOST_ID_BIT+SEQUENCE_BIT;
    public static final long DATACENTER_LEFT_BIAS = HOST_ID_BIT+SEQUENCE_BIT;
    public static final long HOST_LEFT_BIAS = SEQUENCE_BIT;

    private long dataCenterId;
    private long hostId;
    private LongAdder sequenceId = new LongAdder();
    //记录上次时间戳，预防时钟回拨
    private long lastTimeStamp = -1;

    public IdGenerator(long dataCenterId, long hostId) {
        //校验参数是否合法
        if(dataCenterId > DATA_CENTER_MAX || hostId > HOST_ID_MAX){
            throw new IllegalArgumentException("传入的参数（数据中心编号或主机号）非法");
        }
        this.dataCenterId = dataCenterId;
        this.hostId = hostId;
    }

    /**
     * 生成全局唯一Id
     * @return
     */
    public long getId(){
        long currentTime = System.currentTimeMillis();
        long timeStamp = currentTime - TIME_BEGINING;
        //判断时钟是否回拨
        if(timeStamp < lastTimeStamp){
            throw new RuntimeException("服务器进行时钟回拨");
        }
        if(timeStamp == lastTimeStamp){
            sequenceId.increment();
            if(sequenceId.sum() >= SEQUENCE_MAX){
                //1ms内id数量过多，则获取下个时间的时间戳
                timeStamp = getNextTimeStamp();
            }
        }else{
            sequenceId.reset();
        }
        lastTimeStamp = timeStamp;
        long sequence = sequenceId.sum();
        return timeStamp << TIME_STAMP_LEFT_BIAS | dataCenterId << DATACENTER_LEFT_BIAS
                | hostId << HOST_LEFT_BIAS | sequence;
    }

    private long getNextTimeStamp() {
        long current = System.currentTimeMillis()-TIME_BEGINING;
        while(current == lastTimeStamp){
            current = System.currentTimeMillis()-TIME_BEGINING;
        }
        return current;
    }
}
