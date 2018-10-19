package com.dataeye.novel.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dataeye.adutils.utils.httpclient.DeHttpClientUtil;
import com.dataeye.adutils.utils.httpclient.HttpClientContentFormatEnum;
import com.dataeye.adutils.utils.httpclient.HttpClientResponse;
import javafx.scene.control.TextInputControl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class TestBookChapter {


    public static void main(String[] args) {

        String url = "https://book.qidian.com/ajax/book/category?_csrfToken=7hCR8kSljJ8npKk5UEoHDa5VKCnqVmqeUEGDZbb7&bookId=1005236478";

        Map<String, String> headerMap = new HashMap<String, String>() {{
            put("_csrfToken", "7hCR8kSljJ8npKk5UEoHDa5VKCnqVmqeUEGDZbb7");
        }};
        HttpClientResponse httpClientResponse = DeHttpClientUtil.get(url, headerMap,

                HttpClientContentFormatEnum.STRING, null);


        String json = httpClientResponse.getStringContent();
        ;

        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("vs");
        for (Object object : jsonArray) {
            JSONObject j = (JSONObject) object;
            JSONArray cs = j.getJSONArray("cs");
            for (Object o : cs) {
                JSONObject item = (JSONObject) o;
                System.out.println(item.getString("cN"));
            }
        }
    }
}
