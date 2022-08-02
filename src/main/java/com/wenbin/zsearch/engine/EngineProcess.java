package com.wenbin.zsearch.engine;

/**
 *   引擎处理阶段
 *
 *   @Author wenbin
 */
public interface EngineProcess {

    /**
     * 处理
     */
    void process(String url) throws Exception;
}
