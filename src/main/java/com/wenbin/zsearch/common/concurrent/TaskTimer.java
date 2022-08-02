package com.wenbin.zsearch.common.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *   定时器
 *
 *   @Author wenbin
 */
public class TaskTimer {

    // 实际量很少，不会产生无界问题，所以不需要再造轮子去实现了
    private static ScheduledExecutorService scheduledExecutorService = Executors
            .newScheduledThreadPool(2, new NamedThreadFactory("Time-Task-"));

    /**
     * 增加定时任务
     * @param runnable
     */
    public void add(Runnable runnable, int initialDelay, int delay, TimeUnit timeUnit) {
        scheduledExecutorService.scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
    }
}
