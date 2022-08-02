package com.wenbin.zsearch.common.io;

import java.io.File;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *   文件名称按顺序增长
 *
 *   @Author wenbin
 */
public class FileNameSeqGrowthCreator {

    /**
     * 获取最早生成的文件，序号最小
     * @param dictionary
     * @param tempFileName
     * @return
     */
    public File getEarliestFile(File dictionary, String tempFileName) {
        if (dictionary == null) {
            return null;
        }

        File[] files = dictionary.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        //文件不会太多，bf暴力匹配
        int lowest = Integer.MAX_VALUE;
        File result = null;
        int matchSize = tempFileName.length();
        for (File file : files) {
            String digitStr = getMatchedDigitString(tempFileName, matchSize, file);
            if (digitStr == null) {
                continue;
            }

            int digit = Integer.parseInt(digitStr);
            if (digit > lowest) {
                continue;
            }

            lowest = digit;
            result = file;
        }

        return result;
    }


    /**
     * 生成新的临时文件
     * @param dictionary
     * @param tempFileName
     * @return
     */
    public String getFileName(File dictionary, String tempFileName) {
        int seq = getMaxSeq(dictionary, tempFileName) + 1;
        return tempFileName + seq;
    }

    /**
     * 获取最大的文件序号
     * @param dictionary
     * @param tempFileName
     * @return
     */
    public int getMaxSeq(File dictionary, String tempFileName) {
        if (dictionary == null) {
            throw new IllegalArgumentException("文件路径不存在");
        }

        File[] files = dictionary.listFiles();
        if (files == null || files.length == 0) {
            return 0;
        }

        //文件不会太多，bf暴力匹配
        int max = Integer.MIN_VALUE;
        int matchSize = tempFileName.length();
        for (File file : files) {
            String digitStr = getMatchedDigitString(tempFileName, matchSize, file);
            if (digitStr == null) {
                continue;
            }

            int digit = Integer.parseInt(digitStr);
            if (digit < max) {
                continue;
            }

            max = digit;
        }

        return max;
    }

    /**
     * 获取匹配到的数字部分字符串
     * @param tempFileName
     * @param matchSize
     * @param file
     * @return
     */
    private String getMatchedDigitString(String tempFileName, int matchSize, File file) {
        if (!tempFileName.equalsIgnoreCase(file.getName().substring(0, matchSize))) {
            return null;
        }

        String digitStr = file.getName().substring(matchSize);
        if (!NumberUtils.isDigits(digitStr)) {
            return null;
        }

        return digitStr;
    }


    /**
     * 获取最晚生成的文件
     * @param dictionary
     * @param fileNamePrefix
     * @return
     */
    public String getLastest(File dictionary, String fileNamePrefix) {
        return fileNamePrefix + getMaxSeq(dictionary, fileNamePrefix);
    }
}
