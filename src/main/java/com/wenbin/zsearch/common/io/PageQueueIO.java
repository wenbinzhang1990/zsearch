package com.wenbin.zsearch.common.io;

import com.wenbin.zsearch.common.SystemPropertiesUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   页面地址队列io处理对象
 *
 *   @Author wenbin
 */
public class PageQueueIO {

    /**
     * 文件名称生成器
     */
    FileNameSeqGrowthCreator fileNameSeqGrowthCreator = new FileNameSeqGrowthCreator();

    FileIO fileIO;


    public PageQueueIO() {
        this.fileIO = new FileIO();
    }

    /**
     * 临时队列key名称
     */
    private static final String PATH_KEY = "file.page.queue.temp.path";

    /**
     * 临时队列文件前缀
     */
    private static final String TEMP_FILE_NAME = "temp_queue_";

    /**
     * 临时队列数据分割符
     */
    private static final String SPLIT_CHARSET = ";";

    Logger logger = LoggerFactory.getLogger(PageQueueIO.class);

    /**
     * 临时数据刷盘
     * @param tempQueue
     */
    public void flush(List<String> tempQueue) throws IOException {
        String data = parseText(tempQueue);
        File dictionary = new File(SystemPropertiesUtil.getInstance().getValue(PATH_KEY));
        fileIO.flush(data, fileNameSeqGrowthCreator.getFileName(dictionary, TEMP_FILE_NAME));
    }

    /**
     * 转化文本
     * @param tempQueue
     * @return
     */
    private String parseText(List<String> tempQueue) {
        StringBuilder sb = new StringBuilder();
        for (String s : tempQueue) {
            sb.append(s);
            sb.append(SPLIT_CHARSET);
        }

        return sb.toString();
    }

    /**
     * 获取临时队列数据
     * @return
     */
    public List<String> getQueue() throws FileNotFoundException {
        List<String> result = new ArrayList<>();
        // 需要加锁，避免重复获取
        synchronized (this) {
            File dictionary = new File(SystemPropertiesUtil.getInstance().getValue(PATH_KEY));
            //取到正在处理中的文件，需要继续获取下一个
            File file = null;

            file = fileNameSeqGrowthCreator.getEarliestFile(dictionary, TEMP_FILE_NAME);
            if (file == null) {
                return result;
            }

            try (Scanner sc = new Scanner(new FileReader(file))) {
                sc.useDelimiter("|");
                //按分隔符读取字符串
                while (sc.hasNext()) {
                    result.add(sc.next());
                }
            }

            // 简单处理吧，理论上需要爬取完才删除
            if (!file.delete()) {
                logger.error(file.getName() + "删除失败");
            }
        }

        return result;
    }
}
