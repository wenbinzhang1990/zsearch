package com.wenbin.zsearch.common.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *   倒排索引缓冲池
 *
 *   @Author wenbin
 */
public class PageIndexBuffered extends PageIndexIO {

    // todo 简单处理，无脑加，理论上需要淘汰数据的，这个是最大的索引了
    private Map<Integer, WordIdAndPageIdIndex> buffer = new HashMap<>();

    /**
     * 获取数据
     * @param offset
     * @return
     */
    @Override
    public Map<Integer, WordIdAndPageIdIndex> getData(Map<Integer, Integer> offset) throws IOException {
        Map<Integer, WordIdAndPageIdIndex> result = new HashMap<>();
        Map<Integer, Integer> unCachedOffset = new HashMap<>();
        for (Entry<Integer, Integer> entry : offset.entrySet()) {
            WordIdAndPageIdIndex index = buffer.get(entry.getKey());
            if (index != null) {
                result.put(entry.getKey(), index);
            } else {
                unCachedOffset.put(entry.getKey(), entry.getValue());
            }
        }

        // todo 疑问：整个文件缓存下来，太大了不合适，所以加了偏移值快速定位，但是只根据偏移值去查，每次都会涉及io操作，
        //  感觉没有直接整个文件缓存下来性价比高，如何取得一个平衡,可以使用多级缓存解决
        Map<Integer, WordIdAndPageIdIndex> unCachedOffsetData = super.getData(offset);
        result.putAll(unCachedOffsetData);
        buffer.putAll(unCachedOffsetData);
        return result;
    }
}
