package com.dataeye.novel.component;

import com.dataeye.adutils.utils.EmptyChecker;
import com.dataeye.novel.common.NovelListCtx;
import com.dataeye.novel.common.NovelListUrlConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 配置扫描器
 *
 * @author will.tan
 * @version 1.0 2018/4/14 14:09
 */
@Component
public class NovelUrlScanner implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<? extends Object> clazz = bean.getClass();
        if (!clazz.isAnnotationPresent(NovelUrlCfg.class)) {
            return bean;
        }

        //获取注解信息
        NovelUrlCfg novelUrlCfg = clazz.getAnnotation(NovelUrlCfg.class);
        int siteId = novelUrlCfg.siteId();
        String url = novelUrlCfg.url();
        String urlRegex = novelUrlCfg.urlRegex();

        //检查注解的使用是否准确
        if (EmptyChecker.isAllEmpty(urlRegex, url)) {
            throw new RuntimeException("类 " + clazz.getName() + "注解NovelUrlCfg使用错误,urlRegex和url必须设置一个");
        }

        if (EmptyChecker.isAllNotEmpty(urlRegex, url)) {
            throw new RuntimeException("类 " + clazz.getName() + "注解NovelUrlCfg使用错误,urlRegex和url只能设置一个");
        }

        try {
            Method processNovelListResponseMethod = clazz.getMethod("processNovelListResponseMethod", NovelListCtx
                    .class);
            if (EmptyChecker.isNotEmpty(url)) {
                NovelListUrlConfig novelListUrlConfig = new NovelListUrlConfig(siteId, url, bean,
                        processNovelListResponseMethod);
                NovelListUrlMatcher.addNovelListUrlCfg(novelListUrlConfig);
            }

            if (EmptyChecker.isNotEmpty(urlRegex)) {
                NovelListUrlConfig novelListUrlConfig = new NovelListUrlConfig(siteId, urlRegex, bean,
                        processNovelListResponseMethod);
                NovelListUrlMatcher.addRegularNovelUrlCfg(novelListUrlConfig);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return bean;
    }
}
