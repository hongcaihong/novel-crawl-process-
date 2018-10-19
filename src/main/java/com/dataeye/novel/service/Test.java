package com.dataeye.novel.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {

    public static void  main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://www.qidian.com/mm/all").get();

            String title = doc.title();
            System.out.println(doc.getElementById("page-container").attr("data-pagemax"));
            //获取书籍列表信息div
            Elements books = doc.getElementsByClass("all-book-list");

            //获取列表中所有书籍li列表
            Elements lis = books.select("li");

            for (int i = 0; i < lis.size(); i++) {
                //每本书信息
                Element e = lis.get(i);

                Elements divs = e.getElementsByTag("div");
                //bookUrl  //book.qidian.com/info/1003517327 去除//
                String bookUrl = divs.get(0).select("a").attr("href").substring(2).trim();
                //siteBookId
                String siteBookId =bookUrl.substring(21);
               // System.out.println("siteBookId:" +siteBookId);

                //cover 封面url
                String cover = divs.get(0).select("img").attr("src").substring(2);

              //  System.out.println(divs.get(1).select("a"));
              //  System.out.println(divs.get(1).select("a").get(1).html());
             //   System.out.println(divs.get(1).select("p").get(0).select("span").html());
           //     System.out.println(divs.get(1).select("p").get(2).select("span").html());
             //   System.out.println(divs.get(1).select("p").get(2).select("span").get(1).html());
                //书名
                String bookName = divs.get(1).select("a").get(0).html();
                //作者
                String autor = divs.get(1).select("a").get(1).html();

                //siteId  关联

                //types
                String types =  divs.get(1).select("a").get(2).html();
                // channel 女生频道
                String channel = "2";
                // words  网站加密
               String words = divs.get(1).select("p").get(2).select("span").get(1).html();
                System.out.println("words:" +words);
                //finish
                String finish =divs.get(1).select("p").get(0).select("span").html();
                //publishTime
                String publishTime ="";
                //info  简介
                String  info = divs.get(1).select("p").get(1).html();
                // hero 猪脚
                String hero ="";
                System.out.println("---------------------------");
            }
        } catch (Exception e) {

        }


    }
}
