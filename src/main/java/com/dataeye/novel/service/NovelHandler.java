package com.dataeye.novel.service;

import com.alibaba.fastjson.JSON;
import com.dataeye.ad.adcommon.constant.RedisKey;
import com.dataeye.ad.adcommon.domain.NvBook;
import com.dataeye.ad.adcommon.domain.NvBookChapter;
import com.dataeye.ad.adcommon.domain.NvBookUpdate;
import com.dataeye.adutils.utils.EmptyChecker;
import com.dataeye.novel.ApplicationContextUtils;
import com.dataeye.novel.common.DataResult;
import com.dataeye.novel.common.Novel;
import com.dataeye.novel.common.NovelListCtx;
import com.dataeye.novel.common.NovelRequest;
import com.dataeye.novel.mapper.NvBookMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 总入口
 */
public interface NovelHandler {

    String TABLE_NAME = "nv_books_chapter";
    int TOTAL_TABLE_NUM = 100;

    /**
     * 处理小说列表的响应结果
     *
     * @param ctx
     */
    default void processNovelListResponseMethod(final NovelListCtx ctx) {
        //参数合法性校验
        if (ctx == null) {
            return;
        }
        //处理响应结果
        DataResult dataResult = new DataResult();
        parseNovelListResponseContent(ctx, dataResult);
        List<NovelRequest> novelListRequestList = dataResult.getNovelListRequestList();
        List<NovelRequest> novelDetailRequestList = dataResult.getNovelItemRequestList();
        List<Novel> novelList = dataResult.getNovelList();

        if (EmptyChecker.isNotEmpty(novelListRequestList)) {
            novelListRequestList.stream().forEach(request -> {
                getRedisTemplate().opsForList().leftPush(RedisKey.NOVEL_LIST_REQUEST, JSON.toJSONString(request));
            });
        }
        if (EmptyChecker.isNotEmpty(novelDetailRequestList)) {
            novelDetailRequestList.stream().forEach(request -> {
                getRedisTemplate().opsForList().leftPush(RedisKey.NOVEL_DETAIL_REQUEST, JSON.toJSONString(request));
            });
        }
        if (EmptyChecker.isNotEmpty(novelList)) {
            for (Novel novel : novelList) {
                Integer bookId = null;
                NvBook nvBook = novel.getNvBook();
                if (nvBook != null) {
                    Integer siteId = nvBook.getSiteId();
                    String siteBookId = nvBook.getSiteBookId();
                    if (siteId == null || EmptyChecker.isEmpty(siteBookId)) {
                        continue;
                    }
                    getNvBookMapper().insertOrUpdateNvBook(nvBook);
                    NvBook selectResult = getNvBookMapper().getNvBook(siteId, siteBookId);
                    if (selectResult == null) {
                        continue;
                    }
                    bookId = selectResult.getBookId();
                }
                NvBookUpdate bookUpdate = novel.getBookUpdate();
                if (bookUpdate != null) {
                    bookUpdate.setBookId(bookId);
                    getNvBookMapper().insertOrUpdateNvBookUpdate(bookUpdate);
                }
                Integer bookId2 = bookId;
                List<NvBookChapter> chapterList = novel.getChapterList();
                if (EmptyChecker.isNotEmpty(chapterList)) {
                    chapterList.stream().forEach(nvBookChapter -> {
                        nvBookChapter.setBookId(bookId2);
                    });
                    getNvBookMapper().insertOrUpdateNvBookChapter(getRealTableName(bookId2), chapterList);
                }

            }
        }
    }


    default String getRealTableName(Integer bookId) {
        int tableIndex = bookId % TOTAL_TABLE_NUM;
        return TABLE_NAME + "_" + tableIndex;
    }

    /**
     * 解析响应内容，得到原始计划集合
     *
     * @param ctx,dataResult
     * @param
     */

    void parseNovelListResponseContent(final NovelListCtx ctx, DataResult dataResult);


    default StringRedisTemplate getRedisTemplate() {
        return ApplicationContextUtils.getBean(StringRedisTemplate.class);
    }

    default NvBookMapper getNvBookMapper() {
        return ApplicationContextUtils.getBean(NvBookMapper.class);
    }
}
