package com.dataeye.novel.common;

import com.alibaba.fastjson.JSON;
import com.dataeye.adutils.utils.EmptyChecker;
import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ivantan
 * @since 2018/10/17 13:11
 **/
@Data
public class NovelRequest {
    private String method = "GET";
    private String url;
    private String header;
    private String postBody;

    public Map<String, String> getHeaderMap() {
        Map<String, String> map = new HashMap<>();
        if (EmptyChecker.isNotEmpty(header)) {
            try {
                Map map1 = JSON.parseObject(header, Map.class);
                map.putAll(map1);
            } catch (Exception e) {

            }
        }
        return map;
    }
}

