package com.hellozjf.test.test12306.schedule;

import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.service.IQuestionAnswer;
import com.hellozjf.test.test12306.util.IpUtils;
import com.hellozjf.test.test12306.util.JpgUtils;
import com.hellozjf.test.test12306.vo.QuestionInfoVO;
import com.hellozjf.test.test12306.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.File;

/**
 * @author Jingfeng Zhou
 *
 * 这个一个非常鸡贼的定时器，它会每分钟访问http://103.46.128.47:47720/，将结果存储到数据库中
 *
 */
@Component
@Slf4j
public class AutoCheckSchedule {

    @Autowired
    private IQuestionAnswer questionAnswer;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private RestTemplate restTemplate;

//    @Scheduled(fixedRate = 60000)
    public void check() {
        try {
            HttpSession httpSession = new MockHttpSession();

            // 首先从12306获取图片
            ResultVO resultVO = questionAnswer.getQuestion(httpSession);
            if (resultVO.getCode() != 0) {
                log.error("getQuestion error");
                return;
            }
            QuestionInfoVO questionInfoVO = (QuestionInfoVO) resultVO.getData();

            // 把获取到的图片发送到http://103.46.128.47:47720/，获取选择结果
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Forwarded-For", IpUtils.getRandomIp());
            log.debug("X-Forwarded-For = {}", headers.get("X-Forwarded-For"));
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            FileSystemResource resource = new FileSystemResource(JpgUtils.getFullJpeg(new File(customConfig.getForder12306() + "/" + questionInfoVO.getFolderName())));
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("file", resource);
            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);
//            String result = restTemplate.postForObject("http://103.46.128.47:47720/", httpEntity, String.class);
            String result = restTemplate.postForObject("http://littlebigluo.qicp.net:47720/", httpEntity, String.class);
            log.debug("result = {}", result);

            int i = result.indexOf("<B>");
            if (i != -1) {
                i += 3;
                StringBuilder choose = new StringBuilder();
                while (true) {
                    char c = result.charAt(i++);
                    if (c == '<') {
                        break;
                    }
                    choose.append(c);
                }
                result = choose.toString().replace(' ', ',');
                log.debug("choose = {}", result);
            } else {
                log.debug("get auto check failed");
                return;
            }

            // 将结果发送给12306进行验证
            questionInfoVO.setChoose(result);
            resultVO = questionAnswer.answerQuestion(questionInfoVO, false, httpSession);
            if (resultVO.getCode() == 0) {
                log.debug("auto check correct");
            } else {
                log.debug("auto check failed");
            }
        } catch (RestClientException e) {
            log.error("e = {}", e);
        }
    }
}
