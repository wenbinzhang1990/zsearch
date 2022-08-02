package com.wenbin.zsearch.common.http;

import com.alibaba.fastjson.JSON;
import com.wenbin.zsearch.common.io.ErrorIO;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   网页爬取
 *
 *   @Author wenbin
 */
public class HttpUsage {

    public static final String APPLICATION_JSON_VALUE = "application/json";
    private static final Logger logger = LoggerFactory.getLogger(HttpUsage.class);
    private static final Integer CONN_TIME_OUT = 3000;// 超时时间豪秒
    private static final Integer SOCKET_TIME_OUT = 10000;

    /** 每个路由的最大请求数，默认2 */
    private static final Integer DEFAULT_MAX_PER_ROUTE = 40;

    /** 最大连接数，默认20 */
    private static final Integer MAX_TOTAL = 400;

    ErrorIO errorIO = new ErrorIO();

    private static HttpClient httpClient;

    static {
        // 请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONN_TIME_OUT)
                .setConnectionRequestTimeout(CONN_TIME_OUT)
                .setSocketTimeout(SOCKET_TIME_OUT)
                .setRedirectsEnabled(true)
                .build();

        // 管理 http连接池
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        cm.setMaxTotal(MAX_TOTAL);

        httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
    }

    public String requestGet(String url, Map<String, String> paramsMap) throws Exception {
        logger.info("GET request  url:{} params:{}", url, paramsMap);

        Long start = System.currentTimeMillis();

        List<NameValuePair> params = initParams(paramsMap);
        // Get请求
        HttpGet httpGet = new HttpGet(url);

        try {
            // 设置参数
            String str = EntityUtils.toString(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            String uriStr = StringUtils.isEmpty(str) ? httpGet.getURI().toString() : httpGet.getURI().toString() + "?" + str;
            httpGet.setURI(new URI(uriStr));
            // 发送请求
            try {
                HttpResponse response = httpClient.execute(httpGet);
                logger.info("GET request  url:{} response:{} time:{}",
                        url, response, System.currentTimeMillis() - start);

                // 获取返回数据
                return getSuccessRetFromResp(response, url, JSON.toJSONString(paramsMap));
            } catch (Exception ex) {
                logger.error(",url：" + url, ex);
                errorIO.flush(url);
                return "";
            }
        } finally {
            // 必须释放连接，负责连接用完后会阻塞
            httpGet.releaseConnection();
        }
    }

    /**
     *
     * @param url
     * @param paramsMap
     * @return 响应结果
     */
    public String requestPost(String url, Map<String, String> paramsMap) throws Exception {
        HttpPost httpPost = null;
        try {
            logger.info("POST request  url:{} params:{}", url, paramsMap);
            Long start = System.currentTimeMillis();
            List<NameValuePair> params = initParams(paramsMap);
            httpPost = new HttpPost(url.trim());
            httpPost.setHeader("Accept", "Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpPost.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
            httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
            httpPost.setHeader("Connection", "keep-alive");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");

            String retStr;

            httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));

            HttpResponse response = httpClient.execute(httpPost);

            logger.info("POST request  url:{} response:{}  time:{}",
                    url, response, System.currentTimeMillis() - start);

            if (response.getStatusLine().getStatusCode() == 307) {
                httpPost.releaseConnection();
                Header header = response.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD上
                return requestGet(header.getValue(), null); // 这就是跳转后的地址，再向这个地址发出新申请
            }
            if (response.getStatusLine().getStatusCode() == 302) {
                logger.error("爬取失败302,url：" + url);
                errorIO.flush(url);
                return "";
            }

            retStr = getSuccessRetFromResp(response, url, JSON.toJSONString(paramsMap));
            return retStr;
        } catch (
                Exception ex) {
            logger.error("爬取失败,url：" + url, ex);
            errorIO.flush(url);
            return "";
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }

    }

    /**
     * POST json 格式数据
     *
     */
    public String requestPostJsonStr(String url, String json) throws Exception {

        logger.info("POST request  url:{} params:{}", url, json);

        long start = System.currentTimeMillis();

        HttpPost httpPost = new HttpPost(url);

        try {
            StringEntity entity = new StringEntity(json, Consts.UTF_8);
            entity.setContentType(APPLICATION_JSON_VALUE);

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);

            logger.info("POST request  url:{} response:{}  time:{}", url, response, System.currentTimeMillis() - start);

            return getSuccessRetFromResp(response, url, json);
        } finally {
            // 资源释放
            httpPost.releaseConnection();
        }

    }


    private String getSuccessRetFromResp(HttpResponse response, String url, String params) throws Exception {
        String retStr = "";
        // 检验状态码，如果成功接收数据
        int code = response.getStatusLine().getStatusCode();

        if (code == 200) {
            retStr = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
        } else {
            logger.error("爬取失败,url：" + url + ";状态：" + code);
            this.errorIO.flush(url);
            return "";
        }

        logger.info("Http request retStr:{}. url:{}", retStr, url);
        return retStr;
    }

    private static List<NameValuePair> initParams(Map<String, String> paramsMap) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (paramsMap == null) {
            return params;
        }

        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return params;
    }

    public static void main(String[] args) throws Exception {
        HttpUsage httpUsage = new HttpUsage();
        httpUsage.requestPost("https://haokan.baidu.com/", null);
    }
}
