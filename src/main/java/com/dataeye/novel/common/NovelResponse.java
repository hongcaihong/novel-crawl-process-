package com.dataeye.novel.common;

import lombok.Data;

/**
 * @author ivantan
 * @since 2018/10/17 13:16
 **/
@Data
public class NovelResponse {
    private NovelRequest request;
    private String responseContent;
}
