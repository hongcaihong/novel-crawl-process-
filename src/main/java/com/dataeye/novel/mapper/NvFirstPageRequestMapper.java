package com.dataeye.novel.mapper;

import com.dataeye.novel.common.NovelRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ivantan
 * @since 2018/10/17 17:26
 **/
@Mapper
public interface NvFirstPageRequestMapper {
    /**
     * 获取出所有站点获取第一页书籍列表的请求
     *
     * @return
     */
    @Select("SELECT method,firstPageUrl url,headerMap header,postBody FROM nv_first_page_request")
    List<NovelRequest> getFirstPageRequestList();
}
