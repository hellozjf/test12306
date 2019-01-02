package com.hellozjf.test.test12306.util;

import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.vo.BaiduTokenVO;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jingfeng Zhou
 */
public class BaiduAIUtils {

    /**
     * 获取百度AI接口所需要的token
     * @param customConfig
     * @param restTemplate
     * @return
     */
    public static BaiduTokenVO getBaiduTokenVO(CustomConfig customConfig, RestTemplate restTemplate) {
        // 获取识别文字API的token
        String url = String.format("https://aip.baidubce.com/oauth/2.0/token?grant_type=%s&client_id=%s&client_secret=%s",
                "client_credentials",
                customConfig.getClientId(),
                customConfig.getClientSecret());
        BaiduTokenVO baiduTokenVO = restTemplate.postForObject(url, null, BaiduTokenVO.class);
        return baiduTokenVO;
    }
}
