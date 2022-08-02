package com.wenbin.zsearch.engine.collect;

import com.google.common.base.Strings;
import com.wenbin.zsearch.common.http.HttpUsage;
import com.wenbin.zsearch.common.io.PageIDIO;
import com.wenbin.zsearch.common.io.PageIO;
import com.wenbin.zsearch.engine.EngineContext;
import com.wenbin.zsearch.engine.EngineProcess;
import org.apache.commons.lang3.StringUtils;



/**
 *   页面爬取
 *
 *   @Author wenbin
 */
public class PageSpider implements EngineProcess {

    EngineContext engineContext;

    HttpUsage httpUsage = new HttpUsage();

    PageIO pageIO = new PageIO();
    PageIDIO pageIDIO = new PageIDIO();

    /**
     * 初始化上下文
     * @param engineContext
     */
    public PageSpider(EngineContext engineContext) {
        this.engineContext = engineContext;
    }

    /**
     * 处理
     */
    @Override
    public void process(String url) throws Exception {
        grab(url);
    }

    /**
     * 页面爬取
     */
    public void grab(String url) throws Exception {
        // 爬取页面
        String page = httpUsage.requestPost(url, null);

        if (Strings.isNullOrEmpty(page)) {
            engineContext.setCurrentPage("");
            engineContext.setCurrentPageId(0);
            return;
        }

        //关联唯一id
        long id = getUniqueId();

        // url和id关联关系需要存储
        pageIDIO.flush(id, url);

        // id和具体网页需要存储
        pageIO.flush(id, page);

        // 设置上下文
        engineContext.setCurrentPage(page);
        engineContext.setCurrentPageId(id);
    }

    /**
     * 生成唯一id，简单使用，理论上需要全局唯一id组件来生成
     * @return
     */
    public long getUniqueId() {
        return System.currentTimeMillis() * 1000000L + System.nanoTime() % 1000000L;
    }
}
