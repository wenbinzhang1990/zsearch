package com.wenbin.zsearch.engine.collect;

import com.wenbin.zsearch.common.bloom.BloomFilter;
import com.wenbin.zsearch.common.io.PageQueueIO;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   数据爬取队列，为了避免并发产生的并发，此队列需要在线程线程中使用
 *
 *   @Author wenbin
 */
public class SinglePageQueue {

    Queue<String> queue = new ArrayDeque<>(new ArrayList<>());

    // 临时队列是为了写到文件系统的，最终会一次性写到文件，需要充分利用空间局部性，又因为会大量并发写入，需要考虑并发性能
    // 并发可以通过使用隔离PageQueue来控制，一个线程使用一个PageQueue，这样避免了PageQueue的竞争情况，避免了并发的可能性，所以只需要考虑空间局部性即可
    List<String> tempQueue = new ArrayList<>(MAX_SIZE);

    Logger logger = LoggerFactory.getLogger(SinglePageQueue.class);

    PageQueueIO pageQueueIO;

    /**
     * 队列最大空间
     */
    static final Integer MAX_SIZE = 50000;

    /**
     * 布隆过滤器
     */
    BloomFilter bloomFilter;

    public SinglePageQueue(BloomFilter bloomFilter, PageQueueIO pageQueueIO) {
        this.bloomFilter = bloomFilter;
        this.pageQueueIO = pageQueueIO;
    }

    /**
     * 初始化种子数据
     */
    public void init(List<String> urls) {
        queue.addAll(urls);
        for (String url : urls) {
            bloomFilter.set(url);
        }
    }

    /**
     * 初始化种子数据
     */
    public void init(String url) {
        queue.add(url);
        bloomFilter.set(url);
    }

    /**
     * 新增需要爬取的网页地址
     * @param url
     */
    public void add(String url) throws Exception {
        // 布隆过滤器过滤已爬取数据
        if (bloomFilter.find(url)) {
            logger.info(url + "已存在，无需爬取");
            return;
        }
        // 增加到临时队列
        tempQueue.add(url);
        bloomFilter.set(url);

        // 如果临时队列已满，需要写到文件中，需要加锁
        if (tempQueue.size() >= MAX_SIZE) {
            synchronized (this) {
                if (tempQueue.size() >= MAX_SIZE) {
                    pageQueueIO.flush(tempQueue);
                    tempQueue.clear();
                }
            }
        }
    }

    /**
     * 从队列获取网页地址
     * @return
     */
    public String poll() throws Exception {
        // 从正式队列中获取数据
        if (queue.size() > 0) {
            return queue.poll();
        }

        // 如果正式队列为空，从文件获取数据
        queue.addAll(pageQueueIO.getQueue());

        if (queue.size() > 0) {
            return queue.poll();
        }

        synchronized (this) {
            queue.addAll(tempQueue);
            tempQueue.clear();
        }

        return queue.size() > 0 ? queue.poll() : "";
    }

}
