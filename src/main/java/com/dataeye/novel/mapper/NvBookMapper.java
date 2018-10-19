package com.dataeye.novel.mapper;

import com.dataeye.ad.adcommon.domain.NvBook;
import com.dataeye.ad.adcommon.domain.NvBookChapter;
import com.dataeye.ad.adcommon.domain.NvBookUpdate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ivantan
 * @since 2018/10/17 18:50
 **/
@Mapper
public interface NvBookMapper {
    @Select("SELECT * FROM nv_books WHERE siteId=#{siteId} and siteBookId=#{siteBookId}")
    NvBook getNvBook(@Param("siteId") Integer siteId, @Param("siteBookId") String siteBookId);

    Integer insertOrUpdateNvBook(NvBook nvBook);

    Integer insertOrUpdateNvBookUpdate(NvBookUpdate nvBookUpdate);

    Integer insertOrUpdateNvBookChapter(
            @Param("tableName") String tableName,
            @Param("list") List<NvBookChapter> list);
}
