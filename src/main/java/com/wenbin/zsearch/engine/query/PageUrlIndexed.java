package com.wenbin.zsearch.engine.query;

import com.wenbin.zsearch.common.io.PageIDIO;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *   页面url管理
 *
 *   @Author wenbin
 */
public class PageUrlIndexed {

    PageIDIO pageIO = new PageIDIO();

    private Map<Long, String> cache = new HashMap<>();

    public PageUrlIndexed() throws IOException {
        cache.putAll(pageIO.getData());
    }

    /**
     * 获取链接
     * @return
     */
    public String getUrl(long pageId) {
        return cache.get(pageId);
    }


    /**
     * 获取链接
     * @return
     */
    public Map<Long, String> getUrl(List<Long> pageIds) {
        Map<Long, String> result = new HashMap<>();
        for (Long pageId : pageIds) {
            result.put(pageId, cache.get(pageId));
        }

        return result;
    }
}
