package com.wenbin.zsearch.engine.collect;

import com.wenbin.zsearch.common.SystemPropertiesUtil;
import com.wenbin.zsearch.common.bloom.BloomFilter;
import com.wenbin.zsearch.common.concurrent.TaskTimer;
import com.wenbin.zsearch.common.io.FileIO;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   收集信息需要的布隆过滤器
 *
 *   @Author wenbin
 */
public class CollectBloomFilter extends BloomFilter {

    Logger logger = LoggerFactory.getLogger(CollectBloomFilter.class);


    FileIO fileIO;

    private final TaskTimer taskTimer = new TaskTimer();

    private static final String FILE_NAME = "bloom.bin";
    private static final String FILE_PATH_KEY = "file.page.bloom.path";

    /**
     * 初始化布隆过滤器，文件处理
     */
    public CollectBloomFilter() {
        super(200000000);
        fileIO = new FileIO();
    }

    /**
     * 设置备份策略
     */
    public void setBackUp() {
        taskTimer.add(() -> {
            try {
                fileIO.flush(getBytes(), SystemPropertiesUtil.getInstance().getValue(FILE_PATH_KEY) + FILE_NAME);
            } catch (Exception e) {
                logger.error("bloom filter back up failed", e);
            }
        }, 5, 10, TimeUnit.MINUTES);
    }

    /**
     * 初始化数据
     */
    public void init() throws IOException {
        initBytes(fileIO.readBytes(SystemPropertiesUtil.getInstance().getValue(FILE_PATH_KEY) + FILE_NAME));
    }

    public static void main(String[] args) throws IOException {
        CollectBloomFilter collectBloomFilter = new CollectBloomFilter();
        collectBloomFilter.setBackUp();
        collectBloomFilter.init();
    }
}
