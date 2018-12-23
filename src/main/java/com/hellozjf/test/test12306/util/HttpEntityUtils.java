package com.hellozjf.test.test12306.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

/**
 * @author hellozjf
 */
public class HttpEntityUtils {

    public static HttpEntity getHttpEntity(MediaType mediaType, Map<String, Object> map) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        for (Map.Entry entry : map.entrySet()) {
            multiValueMap.add(entry.getKey().toString(), entry.getValue());
        }
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(multiValueMap, httpHeaders);
        return httpEntity;
    }
}
