package com.dataeye.novel.common;

import lombok.Data;

/**
 * 获取到小说列表的
 * @author ivantan
 * @since 2018/10/17 13:32
 **/
@Data
public class NovelListCtx {
    private int siteId;
    private NovelRequest request;
    private String responseContent;
}
