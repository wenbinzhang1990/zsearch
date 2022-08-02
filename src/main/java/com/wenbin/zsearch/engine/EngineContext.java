package com.wenbin.zsearch.engine;

import com.wenbin.zsearch.engine.analyze.SpiltDictionary;
import com.wenbin.zsearch.engine.collect.SinglePageQueue;
import java.util.ArrayList;
import java.util.List;

/**
 *   上下文
 *
 *   @Author wenbin
 */
public class EngineContext {

    SinglePageQueue singlePageQueue;

    List<EngineProcess> engineProcesses = new ArrayList<>();

    SpiltDictionary spiltDictionary;

    // todo 可以把原始页面和处理过的页面分开，暂时直接修改
    private String currentPage = "";

    private long currentPageId = 0;

    /**
     * 初始化爬取页面队列
     * @param pageQueue
     */
    public EngineContext(SinglePageQueue pageQueue, SpiltDictionary spiltDictionary) {
        this.singlePageQueue = pageQueue;
        this.spiltDictionary = spiltDictionary;
    }

    /**
     * 获取页面队列
     * @return
     */
    public SinglePageQueue getSinglePageQueue() {
        return this.singlePageQueue;
    }

    /**
     * 上下文处理
     */
    public void process(String url) throws Exception {
        for (EngineProcess engineProcess : engineProcesses) {
            engineProcess.process(url);
        }
    }

    /**
     * 增加处理流程
     */
    public void addProcess(EngineProcess engineProcess) {
        this.engineProcesses.add(engineProcess);
    }

    /**
     * 当前处理页面
     * @param page
     */
    public void setCurrentPage(String page) {
        this.currentPage = page;
    }


    /**
     * 获取当前处理页面
     * @return
     */
    public String getCurrentPage() {
        return this.currentPage;
    }

    /**
     * 设置当前页面id
     * @param id
     */
    public void setCurrentPageId(long id) {
        this.currentPageId = id;
    }

    /**
     * 获取当前页面id
     * @return
     */
    public long getCurrentPageId() {
        return this.currentPageId;
    }

    /**
     * 获取分词词典
     * @return
     */
    public SpiltDictionary getSpiltDictionary() {
        return this.spiltDictionary;
    }
}
