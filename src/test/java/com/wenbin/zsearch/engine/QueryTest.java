package com.wenbin.zsearch.engine;

import com.wenbin.zsearch.engine.query.QueryService;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

/**
 *   查询测试
 *
 *   @Author wenbin
 */
public class QueryTest {

    QueryService queryService = new QueryService();

    public QueryTest() throws Exception {
    }

    @Test
    public void query() throws IOException {

        List<String> result = queryService.search("测试一下腾讯");
        result.forEach(System.out::println);
    }
}
