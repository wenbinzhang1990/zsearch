package com.wenbin.zsearch.common.io;

import com.wenbin.zsearch.common.SystemPropertiesUtil;
import java.io.IOException;

/**
 *   分词id文件处理对象
 *
 *   @Author wenbin
 */
public class WordIDIO extends FileIO {

    private static final String URL_ID_PATH = "file.page.id_word";

    private static final String FILE_NAME = "word_id.bin";

    /**
     * 获取数据
     */
    public String read() throws IOException {
        return read(getPath());
    }

    /**
     * 获取文件路径
     * @return
     */
    private String getPath() {
        return SystemPropertiesUtil.getInstance().getValue(URL_ID_PATH) + FILE_NAME;
    }
}
