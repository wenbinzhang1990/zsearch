package com.wenbin.zsearch.common.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *   文件处理
 *
 *   @Author wenbin
 */
public class FileIO {



    /**
     * 文件刷盘，写入到系统中
     * @param data
     */
    public void flush(String data, String filePath) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath,true))) {
            bufferedWriter.write(data);
            bufferedWriter.flush();
        }
    }

    /**
     * 文件刷盘，写入到系统中
     * @param data
     */
    public void flush(byte[] data, String filePath) throws IOException {
        FileOutputStream bufferedWriter = new FileOutputStream(new File(filePath));
        bufferedWriter.write(data);
        bufferedWriter.flush();
    }

    /**
     * 获取数据
     * @return
     */
    public String read(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 获取数据
     * @return
     */
    public byte[] readBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
            return null;
        }

        return Files.readAllBytes(path);
    }
}
