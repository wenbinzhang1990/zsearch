package com.wenbin.zsearch.common.io;

import com.wenbin.zsearch.common.SystemPropertiesUtil;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   页面存储
 *
 *   @Author wenbin
 */
public class PageIO extends FileIO {

    private static final String URL_ID_PATH = "file.page.id_page";

    private static final String FILE_NAME_PREFIX = "doc_page_";

    FileNameSeqGrowthCreator fileNameSeqGrowthCreator = new FileNameSeqGrowthCreator();

    Logger logger = LoggerFactory.getLogger(PageIO.class);


    int count = 0;

    private static final int MAX_PAGE_SIZE = 10000;

    /**
     * 页面存储
     * @param id
     * @param page
     */
    public void flush(long id, String page) throws Exception {
        File dictionary = new File(SystemPropertiesUtil.getInstance().getValue(URL_ID_PATH));
        if (dictionary == null) {
            logger.error("file.page.id_page没有文件夹");
            return;
        }

        String fileName = "";
        if (count >= MAX_PAGE_SIZE) {
            fileName = fileNameSeqGrowthCreator.getFileName(dictionary, FILE_NAME_PREFIX);
            count = 0;
        } else {
            fileName = fileNameSeqGrowthCreator.getLastest(dictionary, FILE_NAME_PREFIX);
        }

        flush(getPageData(id, page), SystemPropertiesUtil.getInstance().getValue(URL_ID_PATH) + fileName);
    }

    /**
     * 组装页面数据格式
     * @param id
     * @param page
     * @return
     */
    private String getPageData(long id, String page) {
        return id + "\t" + page + "\t\n";
    }
}
