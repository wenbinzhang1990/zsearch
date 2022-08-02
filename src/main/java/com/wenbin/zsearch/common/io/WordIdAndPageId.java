package com.wenbin.zsearch.common.io;

/**
 *   单词id和页面id对象
 *
 *   @Author wenbin
 */
public class WordIdAndPageId {
    private int wordId;

    private long pageId;

    public WordIdAndPageId(int wordId, long pageId) {
        this.wordId = wordId;
        this.pageId = pageId;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }
}
