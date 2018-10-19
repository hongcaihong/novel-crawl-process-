package com.dataeye.novel.service;

import com.dataeye.novel.common.DataResult;
import com.dataeye.novel.common.NovelListCtx;
import com.dataeye.novel.common.NovelRequest;
import com.dataeye.novel.component.NovelUrlCfg;
import org.springframework.stereotype.Service;

/**
 * @author ivantan
 * @since 2018/10/17 13:23
 **/
//@Service
//@NovelUrlCfg(url = "www.qidian.com/all")
public class QiDianNovelListHandler implements NovelListHandler {


    @Override
    public void parseNovelListResponseContent(NovelListCtx ctx, DataResult dataResult) {
        //获取下一次要请求的页码
        NovelRequest request = ctx.getRequest();
        request.setHeader("{Referer:"+ request.getUrl() +"}");
        request.setUrl("https://www.qidian.com/all?pageSize=20&page=2");
        dataResult.add2NovelListRequestList(request);

        //获取本页页面里面的书籍列表
        NovelRequest book1 = new NovelRequest();

        book1.setUrl("https://book.qidian.com/info/1010468795");
        dataResult.add2NovelItemRequestList(book1);


        NovelRequest book2 = new NovelRequest();
        book2.setUrl("https://book.qidian.com/info/1004608738");
        dataResult.add2NovelItemRequestList(book2);
    }
}
