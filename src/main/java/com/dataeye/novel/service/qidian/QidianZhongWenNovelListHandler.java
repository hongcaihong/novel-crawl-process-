package com.dataeye.novel.service.qidian;

import com.dataeye.adutils.utils.EmptyChecker;
import com.dataeye.novel.common.DataResult;
import com.dataeye.novel.common.NovelListCtx;
import com.dataeye.novel.common.NovelRequest;
import com.dataeye.novel.component.NovelUrlCfg;
import com.dataeye.novel.service.NovelListHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;


@Service
@NovelUrlCfg(url = "www.qidian.com/all")
public class QidianZhongWenNovelListHandler implements NovelListHandler{

    private static final String QIDIAN_SELECTOR_NOVEL_LIST_PAGINATION = "div.pagination";
    private static final String QIDIAN_SELECTOR_NOVEL_LIST_BOOK = "div.book-img-box > a";
    @Override
    public void parseNovelListResponseContent(NovelListCtx ctx, DataResult dataResult) {

        //获取下一次要请求的页码
        NovelRequest bookListRequest = ctx.getRequest();
        String responseContent = ctx.getResponseContent();
        Document document = Jsoup.parse(responseContent);
        if(document == null){
            return;
        }
        //获取分页列表的div标签，拿到data-pagemax 和data-page
        String refererUrl = bookListRequest.getUrl();

        Elements paginationDiv = document.select(QIDIAN_SELECTOR_NOVEL_LIST_PAGINATION);
        if(EmptyChecker.isNotEmpty(paginationDiv)){
            int pageMax = Integer.parseInt(paginationDiv.get(0).attr("data-pagemax"));
            int curPage = Integer.parseInt(paginationDiv.get(0).attr("data-page"));
            if(curPage == 1){
                //获取第一页完整的标签
                //https://www.qidian.com/all?orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=1
                Elements firstPageLink = document.select("a[data-page=1]");
                String firstPageUrl = "https:" + firstPageLink.get(0).attr("href");
                bookListRequest.setUrl(firstPageUrl);
            }

            if(curPage < pageMax ){
                //还有下一页，设置链接
                int nextPage = curPage + 1;
                //设置反盗链
                bookListRequest.setHeader("{Referer:"+ refererUrl +"}");
                bookListRequest.setUrl(bookListRequest.getUrl().substring(0,refererUrl.lastIndexOf('&')) + "&page=" + nextPage);
                dataResult.add2NovelListRequestList(bookListRequest);
            }
        }


        //获取本页页面里面的书籍列表
        Elements bookList = document.select(QIDIAN_SELECTOR_NOVEL_LIST_BOOK);
        if(EmptyChecker.isNotEmpty(bookList)) {
            for (Element e : bookList) {
                NovelRequest bookRequest = new NovelRequest();
                bookRequest.setHeader("{Referer:" + refererUrl + "}");
                bookRequest.setUrl("https:"+e.attr("href"));
                dataResult.add2NovelItemRequestList(bookRequest);
            }
        }


    }
}
