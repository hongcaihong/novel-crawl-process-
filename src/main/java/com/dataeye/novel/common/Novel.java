package com.dataeye.novel.common;

import com.dataeye.ad.adcommon.domain.NvBook;
import com.dataeye.ad.adcommon.domain.NvBookChapter;
import com.dataeye.ad.adcommon.domain.NvBookUpdate;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ivantan
 * @since 2018/10/17 14:10
 **/
@Data
public class Novel {
    private NvBook nvBook;
    private List<NvBookChapter> chapterList = new ArrayList<>();
    private NvBookUpdate bookUpdate;
}
