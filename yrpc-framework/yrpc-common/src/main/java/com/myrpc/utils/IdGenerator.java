package com.myrpc.utils;

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
    public static final long TIME_BEGINING = DateUtil.get("2023-10-01").getTime();
    public static final int DATA_CENTER_BIT = 5;
    public static final int HOST_ID_BIT = 5;
    public static final int SEQUENCE_BIT = 12;

    // 最大值
    public static final long DATA_CENTER_MAX = ~(-1L << DATA_CENTER_BIT);
    public static final long HOST_ID_MAX = ~(-1L << HOST_ID_BIT);

}
