package com.wenbin.zsearch.engine.query;

import com.wenbin.zsearch.common.io.PageIndexBuffered;
import com.wenbin.zsearch.engine.analyze.SpiltDictionary;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *   搜索
 *   todo 搜索服务应该与爬虫分析服务分开，才足够稳定
 *
 *   @Author wenbin
 */
public class QueryService {

    SpiltDictionary spiltDictionary = new SpiltDictionary();

    PageIndexed pageIndexed = new PageIndexed(new PageIndexBuffered(), new OffsetIndexed());

    PageUrlIndexed pageUrlIndexed = new PageUrlIndexed();

    public QueryService() throws Exception {
    }

    /**
     * 搜索
     * @param word
     * @return
     */
    public List<String> search(String word) throws IOException {
        // 分词
        Set<Integer> wordIds = spiltDictionary.search(word);
        // 根据倒排索引查找pageId
        Map<Long, Integer> pageIds = pageIndexed.getPageIdAndNums(wordIds);
        // 根据pageId查到url
        Map<Long, String> pageUrls = pageUrlIndexed.getUrl(new ArrayList<>(pageIds.keySet()));
        // 根据出现次数排序
        PriorityQueue<Entry<Long, Integer>> pq = new PriorityQueue<>((v1, v2) -> v2.getValue() - v1.getValue());
        pq.addAll(pageIds.entrySet());
        // 返回url
        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pageUrls.get(pq.poll().getKey()));
        }

        return result;
    }
}
