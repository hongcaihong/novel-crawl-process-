package com.dataeye.novel.service.hongxiu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dataeye.ad.adcommon.domain.NvBook;
import com.dataeye.adutils.utils.httpclient.DeHttpClientUtil;
import com.dataeye.adutils.utils.httpclient.HttpClientContentFormatEnum;
import com.dataeye.adutils.utils.httpclient.HttpClientResponse;
import com.dataeye.novel.common.DataResult;
import com.dataeye.novel.common.Novel;
import com.dataeye.novel.common.NovelListCtx;
import com.dataeye.novel.common.NovelRequest;
import com.dataeye.novel.component.NovelUrlCfg;
import com.dataeye.novel.service.NovelListHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@NovelUrlCfg(url = "www.hongxiu.com/all")
public class HongXiuNovelListHandler implements NovelListHandler {


    //最大页码数
    private  int maxPageSize;
    private  int currentPage;
    @Override
    public void parseNovelListResponseContent(NovelListCtx ctx, DataResult dataResult) {

        //获取下一次要请求的页码
        NovelRequest bookListRequest = ctx.getRequest();
        String responseContent = ctx.getResponseContent();

        String json = responseContent;


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

        Document document = Jsoup.parse(responseContent);
        if (document == null) {
            return;
        }
        //小说
        Novel novel = new Novel();
        //小说基本信息
        NvBook nvBook = new NvBook();
        //最大页码
        maxPageSize = Integer.parseInt(document.getElementById("page-container").attr("data-pagemax"));
        //当前页码数
        currentPage = Integer.parseInt(document.getElementById("page-container").attr("data-page"));
        if(currentPage<maxPageSize ){
            //下一页url
            bookListRequest.setHeader("{Referer:"+ bookListRequest.getUrl() +"}");
            bookListRequest.setUrl("https://www.qidian.com/mm/all?pageSize=20&page="+ currentPage+1);
            dataResult.add2NovelListRequestList(bookListRequest);

        }

        /*获取本页页面里面的书籍列表*/
        //获取书籍列表信息div
        Elements books = document.getElementsByClass("all-book-list");
        //获取列表中所有书籍li列表
        Elements lis = books.select("li");
        for (int i = 0; i < lis.size(); i++) {
            //每本书信息
            Element e = lis.get(i);
            Elements divs = e.getElementsByTag("div");
            //bookUrl  //book.qidian.com/info/1003517327 加上前缀https:
            String bookUrl =  "https:" + divs.get(0).select("a").attr("href").trim();
            nvBook.setBookUrl(bookUrl);
            //siteBookId
            nvBook.setSiteBookId(bookUrl.substring(bookUrl.indexOf("info/")+5));
            //cover 封面url
            nvBook.setCover(divs.get(0).select("img").attr("src").substring(2));
            //书名
            nvBook.setBookName(divs.get(1).select("a").get(0).html());
            //作者
            nvBook.setAuthor(divs.get(1).select("a").get(1).html());
            //siteId  关联
            nvBook.setSiteId(111111);
            //types
            nvBook.setTypes(divs.get(1).select("a").get(2).html());
            // channel 女生频道
            nvBook.setChannel(2);
            //DOTO  words  网站加密
            String words = divs.get(1).select("p").get(2).select("span").get(1).html();
            nvBook.setWords("200000");
            //finish
            String finish = divs.get(1).select("p").get(0).select("span").html().trim();
            if (finish.equals("完本")) {
                nvBook.setFinish(1);
            } else {
                //未完结
                nvBook.setFinish(0);
            }
            //publishTime
            nvBook.setPublishTime("");
            //info  简介
            nvBook.setInfo(divs.get(1).select("p").get(1).html());
            // hero 猪脚
            String hero = "";
            nvBook.setHero(hero);


            novel.setNvBook(nvBook);
            dataResult.add2NovelList(novel);

        }
    }
}
