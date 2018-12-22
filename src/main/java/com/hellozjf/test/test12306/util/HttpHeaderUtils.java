package com.hellozjf.test.test12306.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hellozjf
 */
@Slf4j
public class HttpHeaderUtils {

    /**
     * 从HttpHeaders中获取jsessionId
     * @param httpHeaders
     * @return
     */
    public static String getJsessionId(HttpHeaders httpHeaders) {
        List<String> stringList = httpHeaders.get("Set-Cookie");
        for (String string : stringList) {
            log.debug("string = {}", string);

            String jsessionIdPatternString = "^JSESSIONID=([^;]*); Path=.*$";
            Pattern jsessionIdPattern = Pattern.compile(jsessionIdPatternString);
            Matcher m = jsessionIdPattern.matcher(string);
            if (m.matches()) {
                log.debug("jsessionId = {}", m.group(1));
                return m.group(1);
            }
        }
        return null;
    }
}
