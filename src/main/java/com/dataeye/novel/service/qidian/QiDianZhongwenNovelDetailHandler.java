package com.dataeye.novel.service.qidian;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dataeye.ad.adcommon.domain.NvBook;
import com.dataeye.ad.adcommon.domain.NvBookChapter;
import com.dataeye.ad.adcommon.domain.NvBookUpdate;
import com.dataeye.adutils.utils.httpclient.DeHttpClientUtil;
import com.dataeye.adutils.utils.httpclient.HttpClientContentFormatEnum;
import com.dataeye.adutils.utils.httpclient.HttpClientResponse;
import com.dataeye.novel.common.DataResult;
import com.dataeye.novel.common.Novel;
import com.dataeye.novel.common.NovelListCtx;
import com.dataeye.novel.component.NovelUrlCfg;
import com.dataeye.novel.service.NovelDetailHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ivantan
 * @since 2018/10/17 13:23
 **/
@Service
@NovelUrlCfg(urlRegex = "book.qidian.com/info/.*")
public class QiDianZhongwenNovelDetailHandler implements NovelDetailHandler {

    @Override
    public void parseNovelListResponseContent(NovelListCtx ctx, DataResult dataResult) {
        //小说章节计数器
        int count = 0;
        int wordsNum = 0;

        Document document = Jsoup.parse(ctx.getResponseContent());
        if(document == null){
            return;
        }
        Novel novel = new Novel();
        //小说基本信息
        NvBook nvBook = new NvBook();
        //TODO 分配站点ID
        nvBook.setSiteId(1);
        nvBook.setChannel(1);//男频
        //小说链接
        nvBook.setBookUrl(ctx.getRequest().getUrl());
        //小说在该站点的id
        Elements siteBookIdTag = document.select("#bookImg");
        nvBook.setSiteBookId(siteBookIdTag.attr("data-bid"));
        //小说封面
        Elements coverTag = document.select("#bookImg > img");
        nvBook.setCover("https:" + coverTag.get(0).attr("src"));
        //小说书名,类型
        // 0首页 ---- 1类型1 --- 2类型2 --- 3书名
        Elements typeTags = document.select("div.crumbs-nav > span").select("a");

        nvBook.setTypes(typeTags.get(1).text() + "," + typeTags.get(2).text());
        nvBook.setBookName(typeTags.get(3).text());
        //小说作者
        Elements authorTag = document.select("a.writer");
        nvBook.setAuthor(authorTag.get(0).text());



        //<p class="tag"> <span1>---连载  <span2> </p>
        Elements finishTag = document.select("p.tag > span");
        if( finishTag.get(0).text().equals("连载")){
            nvBook.setFinish(0);
        } else{
            nvBook.setFinish(1);
        }

        //<meta name="keywords" content="圣墟,楚风,场域,席勒,异人,兽王,大黑牛,王级强者,挣断,玉虚宫,陆通">
        Elements keywordsTag = document.select("meta[name=keywords]");
        nvBook.setHero(keywordsTag.attr("content"));

        //<div class="book-intro"><p></div>
        Elements infoTag = document.select("div.book-intro > p");
        nvBook.setInfo(infoTag.get(0).text());

        //小说章节列表
        //起点小说章节存在缓存技术。。。通过访问以下链接 获取章节json
        //https://book.qidian.com/ajax/book/category?_csrfToken=7hCR8kSljJ8npKk5UEoHDa5VKCnqVmqeUEGDZbb7&bookId=1005236478
        String url="https://book.qidian.com/ajax/book/category?_csrfToken=7hCR8kSljJ8npKk5UEoHDa5VKCnqVmqeUEGDZbb7&bookId=";
        Map<String,String> headerMap=new HashMap<String,String>(){{
            put("_csrfToken","7hCR8kSljJ8npKk5UEoHDa5VKCnqVmqeUEGDZbb7");
        }};
        HttpClientResponse httpClientResponse = DeHttpClientUtil.get(url + nvBook.getSiteBookId(), headerMap,
                HttpClientContentFormatEnum.STRING, null);

        String chaptersJsonString=  httpClientResponse.getStringContent();;

        JSONObject jsonObject = JSON.parseObject(chaptersJsonString);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("vs");
        for(Object object:jsonArray){
            JSONObject j=(JSONObject) object;
            JSONArray cs = j.getJSONArray("cs");
            for(Object o:cs){
                JSONObject item=(JSONObject)o;
                //章节名
                String chapterTitle = item.getString("cN");
                if(!chapterTitle.matches("第.*章.*")){
                    //章节字数小于1000字
                    continue;
                }
//                {
//                    "data": {
//                    "isPublication": 0,
//                            "salesMode": 1,
//                            "vs": [
//                    {
//                        "vId": 61782501,
//                            "cCnt": 33,
//                            "vS": 0,
//                            "isD": 0,
//                            "vN": "正文卷",
//                            "cs": [
//                        {
//                            "uuid": 1,
//                                "cN": "第一章 没白来",
//                                "uT": "2017-02-14 10:00:00",
//                                "cnt": 3135,
//                                "cU": "wgERCvzKq41Qv4sKnwMhGg2/4QbZdkSWmUZp4rPq4Fd4KQ2",
//                                "id": 351149822,
//                                "sS": 1
//                        },
//                    }
//                }

                count++;
                wordsNum += item.getIntValue("cnt");
                NvBookChapter nvBookChapter = new NvBookChapter();
                //TODO 分卷问题
                nvBookChapter.setTitle1("");
                nvBookChapter.setTitle2(chapterTitle);
                //发布时间
                if(count == 1){
                    nvBook.setPublishTime(item.getString("uT"));
                }
                nvBookChapter.setPublishTime(item.getString("uT"));
                nvBookChapter.setChapterUrl("https://read.qidian.com/chapter/"+ item.getString("cU"));
                novel.getChapterList().add(nvBookChapter);
            }
        }
        //小说更新信息
        NvBookUpdate bookUpdate = new NvBookUpdate();
        bookUpdate.setChapterNum(count);
        nvBook.setWords("" + wordsNum);
        if(count > 0) {
            bookUpdate.setLastUpdateTime(novel.getChapterList().get(count - 1).getPublishTime());
        } else {
            bookUpdate.setLastUpdateTime(null);
        }
        //TODO 补充剩余的小说更新信息
        novel.setNvBook(nvBook);
        novel.setBookUpdate(bookUpdate);
        dataResult.add2NovelList(novel);
    }

}
