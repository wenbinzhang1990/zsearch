package com.wenbin.zsearch.common;

import java.io.IOException;
import java.util.Properties;

/**
 *   系统属性工具
 *
 *   @Author wenbin
 */
public class SystemPropertiesUtil {

    private Properties props;

    /**
     * 获取单例
     * @return
     */
    public static SystemPropertiesUtil getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    /**
     * 初始化数据
     */
    public void init() throws IOException {
        props = new Properties();
        props.load(SystemPropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties"));
    }

    /**
     * 获取变量值
     * @param key
     * @return
     */
    public String getValue(String key) {
        return props.getProperty(key);
    }

    /**
     * 单例实现
     */
    private enum Singleton {
        /**
         * Singleton
         */
        INSTANCE;

        Singleton() {
            try {
                systemProperties = new SystemPropertiesUtil();
                systemProperties.init();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        private final SystemPropertiesUtil systemProperties;

        public SystemPropertiesUtil getInstance() {
            return this.systemProperties;
        }
    }
}
