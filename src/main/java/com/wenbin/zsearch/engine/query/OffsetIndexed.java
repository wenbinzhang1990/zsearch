package com.wenbin.zsearch.engine.query;

import com.wenbin.zsearch.common.io.IndexOffsetIO;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *   倒排索引偏移量管理
 *
 *   @Author wenbin
 */
public class OffsetIndexed {

    IndexOffsetIO offsetIO = new IndexOffsetIO();

    private Map<Integer, Integer> cache = new HashMap<>();

    public OffsetIndexed() throws IOException {
        cache.putAll(offsetIO.getData());
    }


    /**
     * 获取偏移量
     * @param wordId
     * @return
     */
    public Integer getOffset(Integer wordId) {
        return cache.get(wordId);
    }

    /**
     * 获取偏移量
     * @param wordIds
     * @return
     */
    public Map<Integer, Integer> getOffset(List<Integer> wordIds) {
        Map<Integer, Integer> result = new HashMap<>();
        for (Integer wordId : wordIds) {
            Integer value = cache.get(wordId);
            if (value != null) {
                result.put(wordId, value);
            }
        }

        return result;
    }
}
