package com.dataeye.novel.component;

import com.dataeye.adutils.utils.EmptyChecker;
import com.dataeye.novel.common.NovelListUrlConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author will.tan
 * @version 1.0 2018/4/14 14:47
 */
public class NovelListUrlMatcher {
    /**
     * 精确匹配的
     */
    private static final Map<String, NovelListUrlConfig> NOVEL_LIST_URL_MAP = new HashMap<>();
    private static final Map<String, NovelListUrlConfig> NOVEL_LIST_URLREG_MAP = new HashMap<>();

    /**
     * 添加到URL精确匹配的map
     *
     * @param config
     */
    public static void addNovelListUrlCfg(NovelListUrlConfig config) {
        if (config == null || EmptyChecker.isEmpty(config.getUrl())) {
            return;
        }
        String url = config.getUrl();
        if (NOVEL_LIST_URL_MAP.containsKey(url)) {
            throw new RuntimeException(config.getBean().getClass().getName() + " 上面配置了重复的注解" +
                    "@NovelUrlCfg(url=" + url + ")");
        }
        NOVEL_LIST_URL_MAP.put(url, config);
    }


    /**
     * 添加到URL正则匹配的map
     *
     * @param config
     */
    public static void addRegularNovelUrlCfg(NovelListUrlConfig config) {
        if (config == null || EmptyChecker.isEmpty(config.getUrl())) {
            return;
        }
        String urlRegex = config.getUrl();
        if (NOVEL_LIST_URLREG_MAP.containsKey(urlRegex)) {
            throw new RuntimeException(config.getBean().getClass().getName() + " 上面配置了重复的注解" +
                    "@NovelUrlCfg(urlRegex=" + urlRegex + ")");
        }
        NOVEL_LIST_URLREG_MAP.put(urlRegex, config);
    }


    /**
     * 查找
     *
     * @param hostAndPath
     * @return
     */
    public static NovelListUrlConfig find(String hostAndPath) {
        if (EmptyChecker.isEmpty(hostAndPath)) {
            return null;
        }
        NovelListUrlConfig novelListUrlConfig = NOVEL_LIST_URL_MAP.get(hostAndPath);
        if (novelListUrlConfig != null) {
            return novelListUrlConfig;
        }
        String matchReg = NOVEL_LIST_URLREG_MAP.keySet().stream().filter(reg -> hostAndPath.matches(reg)).findFirst()
                .orElse(null);
        if (EmptyChecker.isNotEmpty(matchReg)) {
            return NOVEL_LIST_URLREG_MAP.get(matchReg);
        }
        return null;
    }
}
