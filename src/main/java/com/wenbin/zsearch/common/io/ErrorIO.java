package com.wenbin.zsearch.common.io;

import com.wenbin.zsearch.common.SystemPropertiesUtil;
import java.io.IOException;

/**
 *   错误文件操作对象
 *
 *   @Author wenbin
 */
public class ErrorIO extends FileIO {

    private static final String ERROR_PATH = "file.page.error";

    private static final String FILE_NAME = "error.bin";

    /**
     * 获取数据
     */
    public String read() throws IOException {
        return read(getPath());
    }

    /**
     * 数据刷盘
     */
    public void flush(String url) throws IOException {
        flush(url + ":", getPath());
    }

    /**
     * 获取文件路径
     * @return
     */
    private String getPath() {
        return SystemPropertiesUtil.getInstance().getValue(ERROR_PATH) + FILE_NAME;
    }
}
