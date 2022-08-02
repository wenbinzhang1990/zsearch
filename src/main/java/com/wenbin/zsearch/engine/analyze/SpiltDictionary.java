package com.wenbin.zsearch.engine.analyze;

import com.wenbin.zsearch.common.ahocorasick.AcTrieTreeChina;
import com.wenbin.zsearch.common.io.WordIDIO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *   分词字典
 *
 *   @Author wenbin
 */
public class SpiltDictionary {



    private WordIDIO wordIDIO = new WordIDIO();

    private static final char EMPTY_CHAR = ' ';

    private AcTrieTreeChina acTrieTreeChina;

    public SpiltDictionary() throws Exception {
        Map<String, Integer> map = initData();
        acTrieTreeChina = new AcTrieTreeChina(map);
    }

    /**
     * 插入数据
     */
    public Map<String, Integer> initData() throws Exception {
        Map<String, Integer> wordMap = new HashMap<>();
        String str = wordIDIO.read();
        int index = 0;
        StringBuilder indexSb = new StringBuilder();
        StringBuilder wordSb = new StringBuilder();
        while (index < str.length()) {
            // 获取id

            while (index < str.length() && str.charAt(index) != '\t') {
                indexSb.append(str.charAt(index));
                index++;
            }

            index++;

            // 获取分词
            while (index < str.length() && str.charAt(index) != '\n') {
                wordSb.append(str.charAt(index));
                index++;
            }

            wordMap.put(wordSb.toString(), Integer.parseInt(indexSb.toString()));
            wordSb.setLength(0);
            indexSb.setLength(0);
            index++;
        }

        return wordMap;
    }

    /**
     * 查询单词
     * @param text
     * @return
     */
    public Set<Integer> search(String text) {
        return acTrieTreeChina.search(text).keySet();
    }

    public static void main(String[] args) throws Exception {
        SpiltDictionary spiltDictionary = new SpiltDictionary();
        spiltDictionary.search("网站导航北门建行，哈哈，湘潭市邮局测试功能");
    }

}
