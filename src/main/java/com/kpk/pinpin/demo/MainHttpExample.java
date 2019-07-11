package com.kpk.pinpin.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.kpk.pinpin.demo.dto.ABook;
import com.kpk.pinpin.demo.utils.http.HttpUtil;
import com.kpk.pinpin.demo.utils.http.JsonUtil;
import com.kpk.pinpin.demo.utils.http.Response;
import com.kpk.pinpin.demo.utils.http.StringUtils;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;

/**
 * DATE 2019/7/11.
 *
 * @author fupeng.
 */
public class MainHttpExample {

    /**
     * 测试http方法
     * @param args
     */
    public static void main(String[] args) {
        // http get
        String path = "https://api.github.com/repos/zeke/get-json/pulls";
        Map<String, Object> param = Maps.newHashMap();
        param.put("per_page",1);
        String response = HttpUtil.get(path, param);
        if (StringUtils.isBlank(response)) {
            System.out.println("blank");
            return;
        }
        Response.Template<ABook> result = JsonUtil.fromJson(response, new TypeReference<Response.Template<ABook>>() {
        });
        if (result != null && result.getData() != null && CollectionUtils.isNotEmpty(result.getData())) {
            result.getData();
        }
        return;
    }

}
