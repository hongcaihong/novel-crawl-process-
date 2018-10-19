package com.dataeye.novel.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovelListUrlConfig {
    private int siteId;
    private String url;
    private Object bean;
    private Method processNovelListResponseMethod;
}