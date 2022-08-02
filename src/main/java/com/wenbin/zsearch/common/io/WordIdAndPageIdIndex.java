package com.wenbin.zsearch.common.io;

import java.util.List;

/**
 *   倒排索引结构
 *
 *   @Author wenbin
 */
public class WordIdAndPageIdIndex {

    private Integer wordId;

    private List<Long> pageIds;

    public Integer getWordId() {
        return wordId;
    }

    public List<Long> getPageIds() {
        return pageIds;
    }

    public WordIdAndPageIdIndex(Integer wordId, List<Long> pageIds) {
        this.wordId = wordId;
        this.pageIds = pageIds;
    }
}
