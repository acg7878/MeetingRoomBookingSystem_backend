package com.example.meetingroombookingsystem.utils;

import java.sql.Timestamp;

public class TimeUtils {

    // 获取当前时间的 Timestamp
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    // 将 long 类型时间戳转换为 Timestamp
    public static Timestamp longToTimestamp(long timeMillis) {
        return new Timestamp(timeMillis);
    }

    // 将 Timestamp 转换为 long 类型时间戳
    public static long timestampToLong(Timestamp timestamp) {
        return timestamp.getTime();
    }

    // 获取指定时间戳的当前时间（毫秒）
    public static Timestamp getCurrentTimestampWithMillis() {
        return new Timestamp(System.currentTimeMillis());
    }
}