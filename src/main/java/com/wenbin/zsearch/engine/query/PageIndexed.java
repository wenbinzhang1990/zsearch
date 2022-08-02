package com.wenbin.zsearch.engine.query;

import com.wenbin.zsearch.common.io.PageIndexBuffered;
import com.wenbin.zsearch.common.io.WordIdAndPageIdIndex;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *   倒排索引查询管理
 *
 *   @Author wenbin
 */
public class PageIndexed {

    // todo 为了防止缓存过大，需要做缓存的淘汰管理
    private Map<Integer, List<Long>> cache = new HashMap<>();

    private PageIndexBuffered pageIndexBuffered;

    private OffsetIndexed offsetIndexed;

    public PageIndexed(PageIndexBuffered pageIndexBuffered, OffsetIndexed offsetIndexed) throws IOException {
        this.pageIndexBuffered = pageIndexBuffered;
        this.offsetIndexed = offsetIndexed;
    }



    /**
     * 获取pageId以及PageId出现的次数
     * @param wordIds
     * @return
     */
    public Map<Long, Integer> getPageIdAndNums(Set<Integer> wordIds) throws IOException {
        Map<Long, Integer> result = new HashMap<>();
        List<Integer> unCachedIds = new ArrayList<>();
        for (Integer id : wordIds) {
            List<Long> value = cache.getOrDefault(id, null);
            if (value != null) {
                for (Long v : value) {
                    result.put(v, result.getOrDefault(v, 0) + 1);
                }
            } else {
                unCachedIds.add(id);
            }
        }

        Map<Integer, Integer> unCacheOffset = offsetIndexed.getOffset(unCachedIds);
        Map<Integer, WordIdAndPageIdIndex> indexMap = pageIndexBuffered.getData(unCacheOffset);
        for (Map.Entry<Integer, WordIdAndPageIdIndex> entry : indexMap.entrySet()) {
            for (Long pageId : entry.getValue().getPageIds()) {
                result.put(pageId, result.getOrDefault(pageId, 0) + 1);
            }
        }

        return result;
    }

}
