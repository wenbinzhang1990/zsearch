package com.wenbin.zsearch.engine.collect;

import com.wenbin.zsearch.common.io.PageQueueIO;
import java.io.IOException;
import org.junit.Test;

/**
 *   Single page queue test
 *
 *   @Author wenbin
 */
public class SinglePageQueueTest {





    @Test
    public void test() throws Exception {
        CollectBloomFilter collectBloomFilter=new CollectBloomFilter();
        collectBloomFilter.init();
        collectBloomFilter.setBackUp();
        SinglePageQueue singlePageQueue=new SinglePageQueue(collectBloomFilter,new PageQueueIO());
        singlePageQueue.add("http://www.qq.com");
        singlePageQueue.add("http://www.qq.com");
        assert singlePageQueue.tempQueue.size()==1;
    }
}
