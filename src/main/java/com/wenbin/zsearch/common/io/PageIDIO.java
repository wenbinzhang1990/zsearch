package com.wenbin.zsearch.common.io;

import com.wenbin.zsearch.common.SystemPropertiesUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *   页面id文件处理
 *   todo:页面多了以后一个文件是不够的，可以通过根据id存放不同文件来解决
 *
 *   @Author wenbin
 */
public class PageIDIO extends FileIO {

    private static final String URL_ID_PATH = "file.page.url_id";

    private static final String FILE_NAME = "doc_id";

    public static final char END_DELIMITER = ';';
    public static final char PRE_DELIMITER = ':';

    /**
     * 数据写入磁盘
     * @param id
     * @param url
     */
    public void flush(long id, String url) throws IOException {
        String data = String.valueOf(id) + PRE_DELIMITER + url + END_DELIMITER;
        flush(data, getPath());

    }

    /**
     * 获取数据
     */
    public Map<Long, String> getData() throws IOException {
        Map<Long, String> result = new HashMap<>();
        String data = read(getPath());
        int index = 0;
        StringBuilder pageIdSb = new StringBuilder();
        StringBuilder urlSb = new StringBuilder();
        while (index < data.length()) {
            while (data.charAt(index) != PRE_DELIMITER) {
                pageIdSb.append(data.charAt(index++));
            }

            index++;
            while (data.charAt(index) != END_DELIMITER) {
                urlSb.append(data.charAt(index++));
            }

            index++;
            result.put(Long.parseLong(pageIdSb.toString()), urlSb.toString());
            pageIdSb.setLength(0);
            urlSb.setLength(0);
        }

        return result;
    }

    /**
     * 获取文件路径
     * @return
     */
    private String getPath() {
        return SystemPropertiesUtil.getInstance().getValue(URL_ID_PATH) + FILE_NAME;
    }
}
