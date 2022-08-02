package com.wenbin.zsearch.common.io;

import com.wenbin.zsearch.common.SystemPropertiesUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *   文件id和分词id关系映射
 *
 *   @Author wenbin
 */
public class WordIdAndPageIdIO extends FileIO {

    private static final String URL_ID_PATH = "file.page.id_word_id_page";

    private static final String FILE_NAME = "id_word_id_page";
    public static final char MID_DELIMITER = ',';
    public static final char LAST_DELIMITER = ';';
    public static final int STORE_UNIT = 10000;

    /**
     * 数据写入磁盘
     * @param pageId
     * @param wordIds
     */
    public void flush(long pageId, List<Integer> wordIds) throws IOException {
        Collections.sort(wordIds);
        // 目前只有不到20万，简单处理
        StringBuilder[] stringBuilders = new StringBuilder[20];
        for (Integer wordId : wordIds) {
            int index = wordId / STORE_UNIT;
            if (stringBuilders[index] == null) {
                stringBuilders[index] = new StringBuilder();
            }

            stringBuilders[index].append(wordId).append(MID_DELIMITER).append(pageId).append(LAST_DELIMITER);
        }
        for (int i = 0; i < stringBuilders.length; i++) {
            if (stringBuilders[i] == null) {
                continue;
            }
            flush(stringBuilders[i].toString(), getPath(i));
        }
    }

    /**
     * 获取文件内数据
     * @param fileName
     * @return
     */
    public List<WordIdAndPageId> getData(String fileName) throws IOException {
        String data = read(getPath(fileName));
        int index = 0;
        StringBuilder wordSb = new StringBuilder();
        StringBuilder pageSb = new StringBuilder();
        List<WordIdAndPageId> result = new ArrayList<>();
        while (index < data.length()) {
            while (data.charAt(index) != MID_DELIMITER) {
                wordSb.append(data.charAt(index));
                index++;
            }

            index++;
            while (data.charAt(index) != LAST_DELIMITER) {
                pageSb.append(data.charAt(index));
                index++;
            }

            index++;
            WordIdAndPageId wordIdAndPageId = new WordIdAndPageId(Integer.parseInt(wordSb.toString()), Long.parseLong(pageSb.toString()));
            result.add(wordIdAndPageId);
            wordSb.setLength(0);
            pageSb.setLength(0);
        }

        return result;
    }


    /**
     * 获取文件路径
     * 为了切小文件，后续如果发现热点词，其实可以有专门的热点词的索引文件
     * @return
     */
    private String getPath(int index) {
        return SystemPropertiesUtil.getInstance().getValue(URL_ID_PATH) + FILE_NAME + "_" + index;
    }

    /**
     * 获取文件路径
     * @return
     */
    private String getPath(String fileName) {
        return SystemPropertiesUtil.getInstance().getValue(URL_ID_PATH) + fileName;
    }

    /**
     * 获取当前文件夹
     * @return
     */
    public List<String> getAllFileNames() {
        File file = new File(SystemPropertiesUtil.getInstance().getValue(URL_ID_PATH));
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }

        List<String> result = new ArrayList<>();
        for (File f : files) {
            if (!f.getName().contains(FILE_NAME)) {
                continue;
            }

            result.add(f.getName());
        }

        return result;
    }
}
