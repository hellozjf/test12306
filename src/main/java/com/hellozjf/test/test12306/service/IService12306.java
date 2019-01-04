package com.hellozjf.test.test12306.service;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * @author Jingfeng Zhou
 */
public interface IService12306 {

    /**
     * 该接口会访问https://kyfw.12306.cn/otn/login/init，并返回ResponseEntity
     * 访问这个接口，主要是为了获取Cookie
     * @return
     */
    ResponseEntity<String> otnLoginInit();

    /**
     * 该接口会访问https://kyfw.12306.cn/passport/web/auth/uamtk，并返回ResponseEntity
     * 访问这个接口，主要是为了获取Cookie
     * @param cookies
     * @return
     */
    ResponseEntity<String> webAuthUamtk(Map<String, Object> cookies);

    /**
     * 该接口会访问https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&0.23335437505509327，并返回ResponseEntity
     * 访问这个接口，主要是为了获取图片和Cookie
     * @param cookies
     * @return
     */
    ResponseEntity<byte[]> passportCaptchaCaptchaImage(Map<String, Object> cookies);

    /**
     * 该接口会访问https://kyfw.12306.cn/passport/captcha/captcha-check，并返回ResponseEntity
     * 访问这个接口，主要是为了获取选择的问题结果是否正确
     * @param answerList
     * @param cookies
     * @return
     */
    ResponseEntity<String> passportCaptchaCaptchaCheck(List<Integer> answerList, Map<String, Object> cookies);

    /**
     * 该接口会访问https://kyfw.12306.cn/passport/web/login，并返回ResponseEntity
     * 访问这个接口，主要是为了实现登录，不过这和买票时的登录是不是一回事还不太清楚
     * @param cookies
     * @return
     */
    ResponseEntity<String> passportWebLogin(Map<String, Object> cookies);

    /**
     * 该接口会将相应返回的Cookie值写入到cookies变量中
     * @param cookies
     * @param setCookies
     */
    void setCookies(Map<String, Object> cookies, List<String> setCookies);
}
