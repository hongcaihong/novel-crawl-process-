package com.dataeye.novel;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author ivantan
 * @since 2018/4/14 16:10
 **/
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext.getParent() == null) {
            CONTEXT = applicationContext;
        }
    }

    public static <T> T getBean(Class<T> clz) {
        return CONTEXT.getBean(clz);
    }

    /**
     * 获取当前环境配置
     *
     * @return
     */
    public static String getActiveProfile() {
        return CONTEXT.getEnvironment().getActiveProfiles()[0];
    }
}