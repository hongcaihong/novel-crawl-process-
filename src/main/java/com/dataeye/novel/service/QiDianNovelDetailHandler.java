package com.dataeye.novel.service;

import com.dataeye.novel.common.DataResult;
import com.dataeye.novel.common.NovelListCtx;
import com.dataeye.novel.component.NovelUrlCfg;
import org.springframework.stereotype.Service;

/**
 * @author ivantan
 * @since 2018/10/17 13:23
 **/
//@Service
//@NovelUrlCfg(urlRegex = "book.qidian.com/info/.*")
public class QiDianNovelDetailHandler implements NovelDetailHandler {

    @Override
    public void parseNovelListResponseContent(NovelListCtx ctx, DataResult dataResult) {
        System.out.println(ctx.getResponseContent());
    }
}
