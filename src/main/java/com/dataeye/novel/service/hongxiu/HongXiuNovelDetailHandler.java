
package com.dataeye.novel.service.hongxiu;

import com.dataeye.ad.adcommon.domain.NvBookChapter;
import com.dataeye.adutils.utils.EmptyChecker;
import com.dataeye.novel.common.DataResult;
import com.dataeye.novel.common.Novel;
import com.dataeye.novel.common.NovelListCtx;
import com.dataeye.novel.component.NovelUrlCfg;
import com.dataeye.novel.service.NovelDetailHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 小说章节信息
 *
 * @author ivantan
 * @since 2018/10/17 13:23
 **/

@Service
@NovelUrlCfg(urlRegex = "www.hongxiu.com/book/.*")
public class HongXiuNovelDetailHandler implements NovelDetailHandler {

    @Override
    public void parseNovelListResponseContent(NovelListCtx ctx, DataResult dataResult) {
        //小说章节计数器
        int count = 0;
        int wordsNum = 0;

        Document document = Jsoup.parse(ctx.getResponseContent());
        if (document == null) {
            return;
        }
        Novel novel = new Novel();

        List<NvBookChapter> chapterList = new ArrayList<>();
        //小说章节信息
        NvBookChapter nvBookChapter = new NvBookChapter();

        Elements divs = document.getElementsByClass("catalog-content-wrap");
        String ss = divs.select("H3").get(0).html();
        String title1 = "标题1";
        if(EmptyChecker.isNotEmpty(ss)){
            int start = ss.indexOf("</a>");
            int end = ss.indexOf("<i>");
            //标题1
           title1 = ss.substring(start + 4, end);
        }
        nvBookChapter.setTitle1(title1);

        Elements lis = divs.select("ul").select("li");
        for (int k = 0; k < lis.size(); k++) {
            //发布时间
            String publicTime = lis.get(k).select("a").attr("title").trim().substring(6, 24);
            nvBookChapter.setPublishTime(publicTime);
            //chapterUrl
            String chapterUrl = lis.get(k).select("a").attr("href").trim().substring(2);
            nvBookChapter.setChapterUrl(chapterUrl);
            //标题2
            String title2 = lis.get(k).select("a").html();
            nvBookChapter.setTitle2(title2);
            chapterList.add(nvBookChapter);
        }
        novel.setChapterList(chapterList);

        dataResult.add2NovelList(novel);
    }

}

