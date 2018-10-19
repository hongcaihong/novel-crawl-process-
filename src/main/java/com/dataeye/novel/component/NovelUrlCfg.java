package com.dataeye.novel.component;

import java.lang.annotation.*;

/**
 * @author will.tan
 * @version 1.0 2018/4/14 14:29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface NovelUrlCfg {
    /**
     * 网站ID
     *
     * @return
     */
    int siteId() default -1;

    /**
     * 精确匹配的URL
     *
     * @return
     */
    String url() default "";

    /**
     * 正则匹配的URL
     *
     * @return
     */
    String urlRegex() default "";
}