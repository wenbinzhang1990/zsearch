package com.wenbin.zsearch.common.bloom;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 *   布隆过滤器
 *
 *   @Author wenbin
 */
public class BloomFilter {

    BitMap bitMap;
    int bitLength;

    public BloomFilter(int bitLength) {
        this.bitLength = bitLength;
        bitMap = new BitMap(bitLength);
    }

    /**
     * 查询数据存在与否
     * @param data
     * @return
     */
    public boolean find(String data) {
        return bitMap.get(getModHash(hash1(data))) && bitMap.get(getModHash(hash2(data))) && bitMap.get(getModHash(hash3(data)));
    }

    private int getModHash(int data) {
        return data % bitLength;
    }


    /**
     * 设置位图位
     * @param data
     */
    public void set(String data) {
        bitMap.set(getModHash(hash1(data)));
        bitMap.set(getModHash(hash2(data)));
        bitMap.set(getModHash(hash3(data)));

    }

    /**
     * 获取字节数组
     * @return
     */
    public byte[] getBytes() {
        return bitMap.getBytes();
    }

    /**
     * 初始化字节数组
     */
    public void initBytes(byte[] bytes) {
        bitMap.initBytes(bytes);
    }

    /**
     * 一次哈希
     * @param data
     * @return
     */
    private int hash1(String data) {
        return Math.abs(Hashing.murmur3_32_fixed().hashString(data, Charsets.UTF_8).hashCode());
    }

    /**
     * 二次哈希
     * @param data
     * @return
     */
    private int hash2(String data) {
        return Math.abs(Hashing.murmur3_128().hashString(data, Charsets.UTF_8).hashCode());
    }

    /**
     * 三次哈希
     * @param data
     * @return
     */
    private int hash3(String data) {
        return Math.abs(Hashing.murmur3_128(Integer.MAX_VALUE).hashString(data, Charsets.UTF_8).hashCode());
    }


}
