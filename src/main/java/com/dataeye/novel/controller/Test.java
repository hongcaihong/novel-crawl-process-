package com.dataeye.novel.controller;

import com.dataeye.novel.mapper.NvBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ivantan
 * @since 2018/10/17 18:54
 **/
@RestController
public class Test {
    @Autowired
    private NvBookMapper nvBookMapper;

    @RequestMapping("/")
    public String test() {
        nvBookMapper.insertOrUpdateNvBook(null);
        return "success";
    }
}
