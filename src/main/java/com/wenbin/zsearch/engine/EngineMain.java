package com.wenbin.zsearch.engine;



import com.wenbin.zsearch.common.SystemPropertiesUtil;
import com.wenbin.zsearch.common.concurrent.NamedThreadFactory;
import com.wenbin.zsearch.common.concurrent.TaskTimer;
import com.wenbin.zsearch.common.io.PageQueueIO;
import com.wenbin.zsearch.engine.analyze.PageAnalyze;
import com.wenbin.zsearch.engine.analyze.SpiltDictionary;
import com.wenbin.zsearch.engine.collect.CollectBloomFilter;
import com.wenbin.zsearch.engine.collect.PageSpider;
import com.wenbin.zsearch.engine.collect.SinglePageQueue;
import com.wenbin.zsearch.engine.index.PageIndexing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   主入口
 *
 *   @Author wenbin
 */
public class EngineMain {

    private volatile boolean stop = false;

    private static final String SEED_URL = "file.page.seed";

    private static final int MAX_SEED_SIZE = 5;

    Logger logger = LoggerFactory.getLogger(EngineMain.class);

    public static Executor executor = new ThreadPoolExecutor(5, 10, 1,
            TimeUnit.MINUTES, new LinkedBlockingDeque<>(1000),
            new NamedThreadFactory("collect-pool-"));

    private final TaskTimer taskTimer = new TaskTimer();

    /**
     * 启动引擎，进行数据收集，分析，索引
     */
    public void start() throws Exception {
        // 初始化布隆过滤器,恢复之前的爬取记录，这里是全局的，因为不同网页衍生出来的需要爬取的网页可能是一致的
        CollectBloomFilter collectBloomFilter = new CollectBloomFilter();
        collectBloomFilter.init();
        collectBloomFilter.setBackUp();

        // 初始化种子地址
        List<String> seedUrls = new ArrayList<>(
                Arrays.asList(StringUtils.split(SystemPropertiesUtil.getInstance().getValue(SEED_URL), ";")));
        if (seedUrls.size() > MAX_SEED_SIZE) {
            throw new IllegalArgumentException("过多种子地址！");
        }

        // 多线程爬取、分析数据
        PageQueueIO pageQueueIO = new PageQueueIO();
        SpiltDictionary spiltDictionary = new SpiltDictionary();
        for (String seedUrl : seedUrls) {
            executor.execute(() -> {
                try {
                    // 初始化爬取队列，这里开始是线程独有的
                    SinglePageQueue pageQueue = new SinglePageQueue(collectBloomFilter, pageQueueIO);
                    // 初始化种子网页地址
                    pageQueue.init(seedUrl);
                    // 初始化上下文
                    EngineContext engineContext = new EngineContext(pageQueue, spiltDictionary);
                    completeEngineData(engineContext);
                    while (true) {
                        String url = pageQueue.poll();
                        if (StringUtils.isEmpty(url)) {
                            break;
                        }

                        // 正式处理
                        engineContext.process(url);
                    }
                } catch (Exception exception) {
                    logger.error("引擎执行失败", exception);
                }
            });
        }


        PageIndexing pageIndexing = new PageIndexing();
        // 异步间断性建立索引
        taskTimer.add(pageIndexing::build, 5, 10, TimeUnit.MINUTES);

        await();
    }

    private void await() throws InterruptedException {
        while (!stop) {
            Thread.sleep(100000);
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    /**
     * 完善搜索引擎数据
     */
    private void completeEngineData(EngineContext engineContext) throws Exception {
        // 爬取网页信息
        PageSpider pageSpider = new PageSpider(engineContext);
        engineContext.addProcess(pageSpider);

        // 分析网页信息
        PageAnalyze pageAnalyzer = new PageAnalyze(engineContext);
        engineContext.addProcess(pageAnalyzer);
    }

    public static void main(String[] args) {
        PageIndexing pageIndexing = new PageIndexing();
        // 异步间断性建立索引
        pageIndexing.build();
    }
}
