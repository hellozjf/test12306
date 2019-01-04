package com.hellozjf.test.test12306.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.repository.VerificationCodeRepository;
import com.hellozjf.test.test12306.service.IService12306;
import com.hellozjf.test.test12306.vo.BaiduTokenVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author Jingfeng Zhou
 */
@Service
public class Service12306Impl implements IService12306 {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private BaiduTokenVO baiduTokenVO;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResponseEntity<String> otnLoginInit() {
        return restTemplate.getForEntity("https://kyfw.12306.cn/otn/login/init", String.class);
    }

    @Override
    public ResponseEntity<String> webAuthUamtk(Map<String, Object> cookies) {
        HttpHeaders requestHttpHeaders;
        ResponseEntity<String> responseEntity;
        requestHttpHeaders = new HttpHeaders();
        for (Map.Entry entry : cookies.entrySet()) {
            requestHttpHeaders.add(HttpHeaders.COOKIE, entry.getKey() + "=" + entry.getValue());
        }
        requestHttpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("appid", "otn");
        responseEntity = restTemplate.exchange(
                "https://kyfw.12306.cn/passport/web/auth/uamtk",
                HttpMethod.POST,
                new HttpEntity<>(map, requestHttpHeaders),
                String.class
        );
        return responseEntity;
    }

    @Override
    public ResponseEntity<byte[]> passportCaptchaCaptchaImage(Map<String, Object> cookies) {
        HttpHeaders requestHttpHeaders;
        requestHttpHeaders = new HttpHeaders();
        for (Map.Entry entry : cookies.entrySet()) {
            requestHttpHeaders.add(HttpHeaders.COOKIE, entry.getKey() + "=" + entry.getValue());
        }
        return restTemplate.exchange(
                "https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&0.23335437505509327",
                HttpMethod.GET,
                new HttpEntity<>(requestHttpHeaders),
                byte[].class
        );
    }

    @Override
    public void setCookies(Map<String, Object> cookies, List<String> setCookies) {
        for (String string : setCookies) {
            String[] s = string.split(";")[0].split("=");
            cookies.put(s[0], s[1]);
        }
    }

    @Override
    public ResponseEntity<String> passportCaptchaCaptchaCheck(List<Integer> answerList, Map<String, Object> cookies) {
        HttpHeaders requestHttpHeaders = new HttpHeaders();
        for (Map.Entry entry : cookies.entrySet()) {
            requestHttpHeaders.add(HttpHeaders.COOKIE, entry.getKey() + "=" + entry.getValue());
        }
        requestHttpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("answer", StringUtils.join(answerList, ","));
        map.add("login_site", "E");
        map.add("rand", "sjrand");
        return restTemplate.exchange(
                "https://kyfw.12306.cn/passport/captcha/captcha-check",
                HttpMethod.POST,
                new HttpEntity<>(map, requestHttpHeaders),
                String.class
        );
    }

    @Override
    public ResponseEntity<String> passportWebLogin(Map<String, Object> cookies) {
        HttpHeaders requestHttpHeaders = new HttpHeaders();
        for (Map.Entry entry : cookies.entrySet()) {
            requestHttpHeaders.add(HttpHeaders.COOKIE, entry.getKey() + "=" + entry.getValue());
        }
        requestHttpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "nbda1121440");
        map.add("password", "Zjf@1234");
        map.add("appid", "otn");
        return restTemplate.exchange(
                "https://kyfw.12306.cn/passport/web/login",
                HttpMethod.POST,
                new HttpEntity<>(map, requestHttpHeaders),
                String.class
        );
    }
}
