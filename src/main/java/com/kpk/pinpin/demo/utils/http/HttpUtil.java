package com.kpk.pinpin.demo.utils.http;

import com.alibaba.fastjson.JSON;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kunpengku
 */
public class HttpUtil {

    public static final int CONNECT_TIMEOUT = 2000;

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(CONNECT_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(CONNECT_TIMEOUT).build();

    public static <T> T post(final String url, final String jsonData, ContentType contentType, final ResponseCallback<T> callback) {
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        HttpPost request = new HttpPost(url);
        if (StringUtils.isNotEmpty(jsonData)) {
            contentType = ContentType.create(contentType.getMimeType(), "UTF-8");
            StringEntity jsonEntity = new StringEntity(jsonData, contentType);
            request.setEntity(jsonEntity);
        }
        return execute(client, request, callback);
    }

    public static <T> T post(final String url, final String jsonData, final ResponseCallback<T> callback) {
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        HttpPost request = new HttpPost(url);
        if (StringUtils.isNotEmpty(jsonData)) {
            StringEntity jsonEntity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);
            request.setEntity(jsonEntity);
        }
        return execute(client, request, callback);
    }

    public static <T> T postStr(final String url, final String jsonData, final ResponseCallback<T> callback) {
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        HttpPost request = new HttpPost(url);
        if (StringUtils.isNotEmpty(jsonData)) {
            StringEntity jsonEntity = new StringEntity(jsonData, ContentType.APPLICATION_FORM_URLENCODED);
            request.setEntity(jsonEntity);
        }
        return execute(client, request, callback);
    }

    /**
     * 描述：返回String类型，post请求json格式
     */
    public static String post(final String url, final String jsonData) {
        long startTime=System.currentTimeMillis();
        return post(url, jsonData, (resultCode, resultJson) -> {
            LOGGER.info("post request url={}, param={}, result={}, time={}ms", url, jsonData, resultJson, (System.currentTimeMillis() - startTime));
            if (resultCode == HttpStatus.SC_OK) {
                return resultJson;
            }
            return null;
        });
    }

    /**
     */
    public static String post(final String url, Map<String, Object> param) {
        long startTime=System.currentTimeMillis();
        return post(url, param, ((resultCode, resultJson) -> {
            LOGGER.info("post request url={}, param={}, result={}, time={}ms", url, param, resultJson, (System.currentTimeMillis() - startTime));
            if (resultCode == HttpStatus.SC_OK) {
                return resultJson;
            }
            return null;
        }
        ));
    }

    /**
     */
    public static <T> T post(final String url, Map<String, Object> param, final ResponseCallback<T> callback) {
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        if (param != null) {
            Set set = param.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                Object value = param.get(key);
                formparams.add(new BasicNameValuePair(key.toString(), value.toString()));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(formparams, UTF_8));
        return execute(client, httpPost, callback);
    }

    /**
     */
    public static String get(String url, Map<String, Object> map) {
        long startTime = System.currentTimeMillis();
        String resultJson = get(url, map, (resultCode, resultResponse) -> {
                if (resultCode == HttpStatus.SC_OK) {
                    return resultResponse;
                }
                return null;
            }
        );
        LOGGER.info("get request url={}, param={}, result={}, time={}ms", url, JSON.toJSONString(map), resultJson, (System.currentTimeMillis() - startTime));
        return resultJson;
    }

    public static <T> T get(final String url, final String paramData, final ResponseCallback<T> callback) {
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        HttpUriRequest request = new HttpGet(StringUtils.isEmpty(paramData) ? url : url + "?" + paramData);
        return execute(client, request, callback);
    }

    public static <T> T get(final String url, Map<String, Object> map, final ResponseCallback<T> callback) {
        return get(url, getParameter(map), callback);
    }

    private static <T> T execute(CloseableHttpClient client, HttpUriRequest request, final ResponseCallback<T> callback) {
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
            int resultCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String resultJson = EntityUtils.toString(entity, UTF_8);
            if (callback != null) {
                return callback.onResponse(resultCode, resultJson);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (callback != null) {
                return callback.onResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.toString());
            }
        } finally {
            if (null != request && !request.isAborted()) {
                request.abort();
            }
            HttpClientUtils.closeQuietly(client);
            HttpClientUtils.closeQuietly(response);
        }
        return null;
    }

    /**
     * 生成get请求参数
     * @param map
     * @return
     */
    public static String getParameter(Map<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        if (map == null || map.isEmpty()) {
            return sb.toString();
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (StringUtils.isNotEmpty(entry.getKey())) {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        // 跳过第一个&
        if (sb.length() > 0) {
            return sb.substring(1);
        }
        return sb.toString();

    }
}
