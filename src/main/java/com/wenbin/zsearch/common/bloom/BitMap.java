package com.wenbin.zsearch.common.bloom;

/**
 *   位图操作，简单实现
 *
 *   @Author wenbin
 */
public class BitMap {

    private static final int UNIT_SIZE = 8;

    /**
     * 位图
     */
    private byte[] bytes;

    /**
     * 位图大小
     */
    private final int bitLength;

    public BitMap(int bitLength) {
        this.bitLength = bitLength;
        this.bytes = new byte[bitLength / UNIT_SIZE + 1];
    }

    /**
     * 设置位
     * @param k
     */
    public void set(int k) {
        int byteIndex = getByteIndex(k);
        bytes[byteIndex] |= 1 << (byteIndex & 7);
    }


    /**
     * 清除位
     * @param k
     */
    public void clear(int k) {
        int byteIndex = getByteIndex(k);
        bytes[byteIndex] &= ~(1 << (byteIndex & 7));
    }

    /**
     * 获取位的数据
     * @param k
     * @return
     */
    public boolean get(int k) {
        int byteIndex = getByteIndex(k);
        return (bytes[byteIndex] & (1 << (byteIndex & 7))) != 0;
    }

    /**
     * 获取byte下标
     * @param k
     * @return
     */
    private int getByteIndex(int k) {
        int result = k;
        // 超过范围，进行取模运算
        if (result > bitLength) {
            result %= bitLength;
        }

        return result / UNIT_SIZE;
    }

    /**
     * 获取字节数组
     * @return
     */
    public byte[] getBytes() {
        return this.bytes;
    }

    /**
     * 初始化字节数组
     */
    public void initBytes(byte[] bytes) {
        if (bytes == null) {
            return;
        }

        System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
    }
}
