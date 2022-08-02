package com.wenbin.zsearch.common.io;

import com.wenbin.zsearch.common.SystemPropertiesUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *   倒排索引文件
 *
 *   @Author wenbin
 */
public class PageIndexIO extends FileIO {

    private static final String PAGE_INDEX_PATH = "file.page.page_index";

    private static final String TEMP_FILE_NAME = "index_temp_";

    private static final String FILE_NAME = "index_";



    private static final int STORE_UNIT = 40000;
    public static final char END_DELIMITER = ';';
    public static final char PRE_DELIMITER = ':';
    public static final char MID_DELIMITER = ',';

    IndexOffsetIO indexOffsetIO = new IndexOffsetIO();


    /**
     * 获取索引文件路径
     * @return
     * @param index
     */
    private String getPath(int index) {
        return SystemPropertiesUtil.getInstance().getValue(PAGE_INDEX_PATH) + FILE_NAME + index;
    }

    /**
     * 获取临时索引文件路径
     * @return
     */
    private String getTempPath(int index) {
        return SystemPropertiesUtil.getInstance().getValue(PAGE_INDEX_PATH) + TEMP_FILE_NAME + index;
    }



    /**
     * 数据刷盘
     * @param indexes
     */
    public void flushData(List<WordIdAndPageIdIndex> indexes) throws IOException {
        // 目前只有不到20万，简单处理
        StringBuilder[] stringBuilders = new StringBuilder[5];
        Arrays.fill(stringBuilders, new StringBuilder());
        StringBuilder offsetSb = new StringBuilder();
        int index = 0;
        for (WordIdAndPageIdIndex wordIdAndPageIdIndex : indexes) {
            offsetSb.append(wordIdAndPageIdIndex.getWordId()).append(":").append(index).append(";");
            StringBuilder sb = stringBuilders[getFileNum(wordIdAndPageIdIndex.getWordId())];
            int original = sb.length();
            sb.append(wordIdAndPageIdIndex.getWordId());
            sb.append(PRE_DELIMITER);
            sb.append(StringUtils.join(wordIdAndPageIdIndex.getPageIds().toArray(), MID_DELIMITER));
            sb.append(END_DELIMITER);
            index += sb.length() - original;
        }

        for (int i = 0; i < stringBuilders.length; i++) {
            flush(stringBuilders[i].toString(), getTempPath(i));
        }

        indexOffsetIO.flush(offsetSb.toString());
    }

    /**
     * 获取数据
     * @param offset
     * @return
     */
    public Map<Integer, WordIdAndPageIdIndex> getData(Map<Integer, Integer> offset) throws IOException {
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (Integer wordId : offset.keySet()) {
            map.computeIfAbsent(getFileNum(wordId), k -> new ArrayList<>());
            map.get(getFileNum(wordId)).add(wordId);
        }

        Map<Integer, WordIdAndPageIdIndex> result = new HashMap<>();
        for (Integer i : map.keySet()) {
            String data = read(getPath(i));
            for (Integer wordId : offset.keySet()) {
                result.put(wordId, getIndex(data, offset.get(wordId)));
            }
        }

        return result;
    }


    /**
     * 获取文件序号
     * @param wordId
     * @return
     */
    protected int getFileNum(Integer wordId) {
        return wordId / STORE_UNIT;
    }

    /**
     * 获取具体的倒排索引内容
     * @param data
     * @param offset
     * @return
     */
    private WordIdAndPageIdIndex getIndex(String data, Integer offset) {
        StringBuilder wordIdSb = new StringBuilder();
        StringBuilder pageIdSb = new StringBuilder();
        List<Long> pageIds = new ArrayList<>();
        while (data.charAt(offset) != PRE_DELIMITER) {
            wordIdSb.append(data.charAt(offset++));
        }

        offset++;
        while (data.charAt(offset) != END_DELIMITER) {
            while (data.charAt(offset) != MID_DELIMITER && data.charAt(offset) != END_DELIMITER) {
                pageIdSb.append(data.charAt(offset++));
            }

            pageIds.add(Long.parseLong(pageIdSb.toString()));
            pageIdSb.setLength(0);
            if (data.charAt(offset) != END_DELIMITER) {
                offset++;
            }
        }

        return new WordIdAndPageIdIndex(Integer.parseInt(wordIdSb.toString()), pageIds);
    }

    /**
     * 临时索引文件开始生效
     */
    public void takeEffect() throws IOException {
        File dictionary = new File(SystemPropertiesUtil.getInstance().getValue(PAGE_INDEX_PATH));
        File[] files = dictionary.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            if (!f.getName().contains("temp")) {
                Files.deleteIfExists(Paths.get(f.getPath()));
            } else {
                f.renameTo(new File(f.getPath().replace("_temp", "")));
            }
        }

        indexOffsetIO.takeEffect();
    }
}
