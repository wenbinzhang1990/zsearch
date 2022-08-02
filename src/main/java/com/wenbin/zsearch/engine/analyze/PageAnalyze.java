package com.wenbin.zsearch.engine.analyze;

import com.wenbin.zsearch.common.io.WordIdAndPageIdIO;
import com.wenbin.zsearch.engine.EngineContext;
import com.wenbin.zsearch.engine.EngineProcess;
import java.util.ArrayList;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *   页面分析
 *
 *   @Author wenbin
 */
public class PageAnalyze implements EngineProcess {

    EngineContext engineContext;

    WordIdAndPageIdIO wordIdAndPageIdIO = new WordIdAndPageIdIO();



    /**
     * 初始化上下文
     * @param engineContext
     */
    public PageAnalyze(EngineContext engineContext) {
        this.engineContext = engineContext;
    }

    /**
     * 页面数据分析
     */
    public void analyze() throws Exception {
        if (StringUtils.isEmpty(engineContext.getCurrentPage()))
        {
            return;
        }

        // 删除无效标签
        Document doc = Jsoup.parse(engineContext.getCurrentPage());
        deleteInvalidChar(doc);
        engineContext.setCurrentPage(doc.outerHtml());

        // 加入爬取队列
        offerUrls(doc);

        // 分词
        Set<Integer> wordIds = engineContext.getSpiltDictionary().search(engineContext.getCurrentPage());

        // 分词id与pageId刷盘
        wordIdAndPageIdIO.flush(engineContext.getCurrentPageId(), new ArrayList<>(wordIds));
    }

    /**
     * 将需要爬取的url入队
     */
    private void offerUrls(Document doc) throws Exception {
        for (Element element : doc.select("link")) {
            String url = element.attr("href");
            if (url.contains(".css")) {
                continue;
            }

            if (!url.contains("http")) {
                continue;
            }

            engineContext.getSinglePageQueue().add(url);
        }

        for (Element element : doc.select("a")) {
            String url = element.attr("href");
            if (!url.contains("http")) {
                continue;
            }

            engineContext.getSinglePageQueue().add(url);
        }
    }

    /**
     * 删除无效字符
     */
    private void deleteInvalidChar(Document doc) {
        doc.select("option").remove();
        doc.select("script").remove();
        doc.select("style").remove();
    }

    /**
     * 阶段处理
     * @throws Exception
     */
    @Override
    public void process(String url) throws Exception {
        analyze();
    }
}
