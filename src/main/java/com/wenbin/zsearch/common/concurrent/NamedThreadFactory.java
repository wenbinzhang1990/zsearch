package com.wenbin.zsearch.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *   可被命名的线程工厂
 *
 *   @Author wenbin
 */
public class NamedThreadFactory implements ThreadFactory {

    private String threadNamePrefix;

    private int threadPriority = 5;
    private boolean daemon = false;

    private final AtomicInteger threadCount = new AtomicInteger(0);

    /**
     * 初始化线程名称前缀
     * @param threadNamePrefix
     */
    public NamedThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, this.nextThreadName());
        thread.setDaemon(this.daemon);
        thread.setPriority(this.threadPriority);
        return thread;
    }

    /**
     * 生成线程名
     * @return
     */
    private String nextThreadName() {
        return this.threadNamePrefix + this.threadCount.incrementAndGet();
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public int getThreadPriority() {
        return this.threadPriority;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }
}
