package com.wenbin.zsearch.engine.index;

import com.wenbin.zsearch.common.io.PageIndexIO;
import com.wenbin.zsearch.common.io.WordIdAndPageId;
import com.wenbin.zsearch.common.io.WordIdAndPageIdIO;
import com.wenbin.zsearch.common.io.WordIdAndPageIdIndex;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *   建立倒排索引
 *
 *   @Author wenbin
 */
public class PageIndexing {

    PageIndexIO pageIndexIO = new PageIndexIO();

    WordIdAndPageIdIO wordIdAndPageIdIO = new WordIdAndPageIdIO();



    public PageIndexing() {
    }

    /**
     * 建立倒排索引
     */
    public void build() {
        try {
            // 获取小文件临时索引
            List<String> fileNames = wordIdAndPageIdIO.getAllFileNames();

            // 合并倒排索引
            List<WordIdAndPageIdIndex> indexes = mergeIndex(fileNames);

            // 数据刷盘
            pageIndexIO.flushData(indexes);

            // 临时索引生效
            pageIndexIO.takeEffect();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * 合并倒排索引
     * 学习使用，一次性处理了，不再多次了
     * 理论上这个文件会较大，需要多次增量处理
     */
    private List<WordIdAndPageIdIndex> mergeIndex(List<String> fileNames) throws IOException {
        Map<Integer, List<Long>> map = new HashMap<>();
        for (String fileName : fileNames) {
            List<WordIdAndPageId> list = wordIdAndPageIdIO.getData(fileName);
            for (WordIdAndPageId w : list) {
                map.computeIfAbsent(w.getWordId(), k -> new ArrayList<>());
                map.get(w.getWordId()).add(w.getPageId());
            }
        }

        List<WordIdAndPageIdIndex> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Long>> entry : map.entrySet()) {
            result.add(new WordIdAndPageIdIndex(entry.getKey(), entry.getValue()));
        }

        return result;
    }


}
