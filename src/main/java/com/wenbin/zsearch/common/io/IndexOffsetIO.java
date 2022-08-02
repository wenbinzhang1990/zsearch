package com.wenbin.zsearch.common.io;

import com.wenbin.zsearch.common.SystemPropertiesUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *   索引偏移操作
 *
 *   @Author wenbin
 */
public class IndexOffsetIO extends FileIO {

    private static final String TEMP_OFFSET_FILE_NAME = "index_temp_offset";

    private static final String FILE_OFFSET_NAME = "index_offset";

    private static final String PAGE_INDEX_OFFSET_PATH = "file.page.page_index_offset";

    public static final char END_DELIMITER = ';';
    public static final char PRE_DELIMITER = ':';

    /**
     * 获取索引偏移文件路径
     * @return
     * @param
     */
    private String getOffsetPath() {
        return SystemPropertiesUtil.getInstance().getValue(PAGE_INDEX_OFFSET_PATH) + FILE_OFFSET_NAME;
    }

    /**
     * 获取临时索引偏移文件路径
     * @return
     */
    private String getTempOffsetPath() {
        return SystemPropertiesUtil.getInstance().getValue(PAGE_INDEX_OFFSET_PATH) + TEMP_OFFSET_FILE_NAME;
    }

    /**
     * 数据存盘
     * @param data
     */
    public void flush(String data) throws IOException {
        flush(data, getTempOffsetPath());
    }

    /**
     * 获取偏移量数据
     * @return
     */
    public Map<Integer, Integer> getData() throws IOException {
        String data = read(getOffsetPath());
        int index = 0;
        Map<Integer, Integer> map = new HashMap<>();
        StringBuilder wordIdSb = new StringBuilder();
        StringBuilder offsetSb = new StringBuilder();
        while (index < data.length()) {
            while (data.charAt(index) != PRE_DELIMITER) {
                wordIdSb.append(data.charAt(index++));
            }

            index++;
            while (data.charAt(index) != END_DELIMITER) {
                offsetSb.append(data.charAt(index++));
            }

            index++;
            map.put(Integer.parseInt(wordIdSb.toString()), Integer.parseInt(offsetSb.toString()));
            wordIdSb.setLength(0);
            offsetSb.setLength(0);
        }

        return map;
    }

    /**
     * 生效
     */
    public void takeEffect() throws IOException {
        Files.deleteIfExists(Paths.get(getOffsetPath()));
        new File(getTempOffsetPath()).renameTo(new File(getTempOffsetPath().replaceAll("_temp", "")));
    }
}
