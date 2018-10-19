package com.dataeye.novel.component;

import com.alibaba.fastjson.JSON;
import com.dataeye.ad.adcommon.annotation.ScheduledJob;
import com.dataeye.ad.adcommon.constant.RedisKey;
import com.dataeye.adutils.utils.EmptyChecker;
import com.dataeye.adutils.utils.HttpUtil;
import com.dataeye.adutils.utils.LogbackUtil;
import com.dataeye.adutils.utils.httpclient.DeHttpClientUtil;
import com.dataeye.adutils.utils.httpclient.HttpClientContentFormatEnum;
import com.dataeye.adutils.utils.httpclient.HttpClientResponse;
import com.dataeye.novel.common.NovelListCtx;
import com.dataeye.novel.common.NovelListUrlConfig;
import com.dataeye.novel.common.NovelRequest;
import com.dataeye.novel.common.NovelResponse;
import com.dataeye.novel.mapper.NvFirstPageRequestMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ivantan
 * @since 2018/10/17 12:49
 **/
@Component
public class Job {

    private static final Logger logger = LogbackUtil.getDefaultLogger("job");

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private NvFirstPageRequestMapper nvFirstPageRequestMapper;

    /**
     * 加载每个网站的第一页书籍列表
     *
     * @throws Exception
     */
    //@ScheduledJob(cron = "0 0 1 * * ?")
    @ScheduledJob(initialDelay = 1000, fixedDelay = 50000000, desc = "")
    public void loadOriginalRequest2Redis() throws Exception {
        List<NovelRequest> firstPageRequestList = nvFirstPageRequestMapper.getFirstPageRequestList();
        logger.info("getFirstPageRequestList->size:{}", firstPageRequestList.size());
        for (NovelRequest request : firstPageRequestList) {
            redisTemplate.opsForList().leftPush(RedisKey.NOVEL_LIST_REQUEST, JSON.toJSONString(request));
        }
    }

    /**
     * 消费获取小说列表的请求
     */
    @ScheduledJob(initialDelay = 3000, fixedDelay = 5000, desc = "")
    public void startConsumeNovelListRequest() {
        while (true) {
            try {
                String requestJson = redisTemplate.opsForList().rightPop(RedisKey.NOVEL_LIST_REQUEST, 1, TimeUnit
                        .SECONDS);
                if (EmptyChecker.isEmpty(requestJson)) {
                    break;
                }
                logger.info("startConsumeNovelListRequest->{}", requestJson);
                NovelRequest request = JSON.parseObject(requestJson, NovelRequest.class);
                String url = request.getUrl();
                Map<String, String> headerMap = request.getHeaderMap();
                String method = request.getMethod();
                if ("GET".equals(method)) {
                    HttpClientResponse httpClientResponse = DeHttpClientUtil.get(url, headerMap,
                            HttpClientContentFormatEnum.STRING, null);
                    String stringContent = httpClientResponse.getStringContent();

                    NovelResponse response = new NovelResponse();
                    response.setRequest(request);
                    response.setResponseContent(stringContent);

                    redisTemplate.opsForList().leftPush(RedisKey.NOVEL_LIST_RESPONSE, JSON.toJSONString(response));
                }
            } catch (Exception e) {
                logger.error("exception", e);
            }
        }
    }

    /**
     * 消费小说列表响应结果
     */
    @ScheduledJob(initialDelay = 6000, fixedDelay = 5000, desc = "")
    public void startConsumeNovelListResponse() {
        while (true) {
            try {
                String responseJson = redisTemplate.opsForList().rightPop(RedisKey.NOVEL_LIST_RESPONSE, 1, TimeUnit
                        .SECONDS);
                if (EmptyChecker.isEmpty(responseJson)) {
                    break;
                }
                logger.info("startConsumeNovelListResponse->{}", responseJson);
                NovelResponse response = JSON.parseObject(responseJson, NovelResponse.class);
                NovelRequest request = response.getRequest();
                String url = request.getUrl();
                String responseContent = response.getResponseContent();

                String hostAndPath = HttpUtil.getHostAndPath(url);
                //通过hostAndPath去找对应的处理器，并反射调用方法
                NovelListUrlConfig novelListUrlConfig = NovelListUrlMatcher.find(hostAndPath);
                if (novelListUrlConfig != null) {
                    Object bean = novelListUrlConfig.getBean();
                    Method method = novelListUrlConfig.getProcessNovelListResponseMethod();
                    if (method != null) {

                        NovelListCtx ctx = new NovelListCtx();
                        ctx.setRequest(request);
                        ctx.setResponseContent(responseContent);
                        ctx.setSiteId(novelListUrlConfig.getSiteId());
                        method.invoke(bean, ctx);
                    }
                }

            } catch (Exception e) {
                logger.error("exception", e);
            }
        }
    }


    /**
     * 消费获取小说详情页的请求
     */
    @ScheduledJob(initialDelay = 10000, fixedDelay = 5000, desc = "")
    public void startConsumeNovelDetailRequest() {
        while (true) {
            try {
                String requestJson = redisTemplate.opsForList().rightPop(RedisKey.NOVEL_DETAIL_REQUEST, 1, TimeUnit
                        .SECONDS);
                if (EmptyChecker.isEmpty(requestJson)) {
                    break;
                }
                logger.info("startConsumeNovelDetailRequest->{}", requestJson);
                NovelRequest request = JSON.parseObject(requestJson, NovelRequest.class);
                String url = request.getUrl();
                Map<String, String> headerMap = request.getHeaderMap();
                String method = request.getMethod();
                if ("GET".equals(method)) {
                    HttpClientResponse httpClientResponse = DeHttpClientUtil.get(url, headerMap,
                            HttpClientContentFormatEnum.STRING, null);
                    String stringContent = httpClientResponse.getStringContent();

                    NovelResponse response = new NovelResponse();
                    response.setRequest(request);
                    response.setResponseContent(stringContent);

                    redisTemplate.opsForList().leftPush(RedisKey.NOVEL_DETAIL_RESPONSE, JSON.toJSONString(response));
                }
            } catch (Exception e) {
                logger.error("exception", e);
            }
        }
    }


    /**
     * 消费小说详情响应结果
     */
    @ScheduledJob(initialDelay = 10000, fixedDelay = 1000, desc = "")
    public void startConsumeNovelDetailResponse() {
        while (true) {
            try {
                String responseJson = redisTemplate.opsForList().rightPop(RedisKey.NOVEL_DETAIL_RESPONSE, 1, TimeUnit
                        .SECONDS);
                if (EmptyChecker.isEmpty(responseJson)) {
                    break;
                }
                logger.info("startConsumeNovelDetailResponse->{}", responseJson);
                NovelResponse response = JSON.parseObject(responseJson, NovelResponse.class);
                NovelRequest request = response.getRequest();
                String url = request.getUrl();
                String responseContent = response.getResponseContent();

                String hostAndPath = HttpUtil.getHostAndPath(url);
                //通过hostAndPath去找对应的处理器，并反射调用方法
                NovelListUrlConfig novelListUrlConfig = NovelListUrlMatcher.find(hostAndPath);
                if (novelListUrlConfig != null) {
                    Object bean = novelListUrlConfig.getBean();
                    Method method = novelListUrlConfig.getProcessNovelListResponseMethod();
                    if (method != null) {
                        NovelListCtx ctx = new NovelListCtx();
                        ctx.setRequest(request);
                        ctx.setResponseContent(responseContent);
                        ctx.setSiteId(novelListUrlConfig.getSiteId());
                        method.invoke(bean, ctx);
                    }
                }

            } catch (Exception e) {
                logger.error("exception", e);
            }
        }
    }


}
