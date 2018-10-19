package com.dataeye.novel.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 从媒体的返回内容里面获取到的结果
 *
 * @author ivantan
 * @since 2018/4/23 9:49
 **/
@Getter
public class DataResult {
    /**
     * 需要继续访问其他页码的url
     */
    private List<NovelRequest> novelListRequestList = new ArrayList<>();
    /**
     * 去继续获取书籍详细信息的请求
     */
    private List<NovelRequest> novelItemRequestList = new ArrayList<>();
    /**
     * 保存获取的小说详情
     */
    private List<Novel> novelList=new ArrayList<>();


    public void add2NovelListRequestList(NovelRequest request) {
        novelListRequestList.add(request);
    }

    public void add2NovelItemRequestList(NovelRequest request) {
        novelItemRequestList.add(request);
    }

    public void add2NovelList(Novel novel) {
        novelList.add(novel);
    }

}
