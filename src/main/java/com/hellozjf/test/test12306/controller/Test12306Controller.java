package com.hellozjf.test.test12306.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.constant.PictureNames;
import com.hellozjf.test.test12306.constant.ResultEnum;
import com.hellozjf.test.test12306.dataobject.VerificationCode;
import com.hellozjf.test.test12306.repository.VerificationCodeRepository;
import com.hellozjf.test.test12306.util.*;
import com.hellozjf.test.test12306.vo.BaiduTokenVO;
import com.hellozjf.test.test12306.vo.QuestionInfoVO;
import com.hellozjf.test.test12306.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

/**
 * @author hellozjf
 */
@Controller
@Slf4j
public class Test12306Controller {

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

    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 获取下一个没有回答的问题
     *
     * @return
     */
    private QuestionInfoVO getNextUncheckQuestion() {

        VerificationCode verificationCode = verificationCodeRepository.findTopByChooseNullOrderByFolderNameAsc();
        if (verificationCode == null) {
            return null;
        }
        String folderName = verificationCode.getFolderName();

        QuestionInfoVO questionInfoVO = new QuestionInfoVO();
        questionInfoVO.setFolderName(folderName);
        String prefix = customConfig.getNetPrefix();
        questionInfoVO.setQuestionUrl(prefix + "/" + folderName + "/question.jpg");
        questionInfoVO.setPic00Url(prefix + "/" + folderName + "/pic00.jpg");
        questionInfoVO.setPic01Url(prefix + "/" + folderName + "/pic01.jpg");
        questionInfoVO.setPic02Url(prefix + "/" + folderName + "/pic02.jpg");
        questionInfoVO.setPic03Url(prefix + "/" + folderName + "/pic03.jpg");
        questionInfoVO.setPic10Url(prefix + "/" + folderName + "/pic10.jpg");
        questionInfoVO.setPic11Url(prefix + "/" + folderName + "/pic11.jpg");
        questionInfoVO.setPic12Url(prefix + "/" + folderName + "/pic12.jpg");
        questionInfoVO.setPic13Url(prefix + "/" + folderName + "/pic13.jpg");
        return questionInfoVO;
    }

    @GetMapping("/getUncheckQuestion")
    @ResponseBody
    public ResultVO getUncheckQuestion(HttpSession httpSession) throws Exception {

        Map<String, Object> cookies = new HashMap<>();

        // 通过login获取jsessionid，然后通过这个jsessionid去获取图片
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://kyfw.12306.cn/otn/login/init", String.class);
        HttpHeaders responseHttpHeaders = responseEntity.getHeaders();
        List<String> setCookies = responseHttpHeaders.get(HttpHeaders.SET_COOKIE);
        for (String string : setCookies) {
            String[] s = string.split(";")[0].split("=");
            cookies.put(s[0], s[1]);
        }

        // 访问https://kyfw.12306.cn/passport/web/auth/uamtk
        HttpHeaders requestHttpHeaders = new HttpHeaders();
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
        responseHttpHeaders = responseEntity.getHeaders();
        setCookies = responseHttpHeaders.get("Set-Cookie");
        for (String string : setCookies) {
            String[] s = string.split(";")[0].split("=");
            cookies.put(s[0], s[1]);
        }

        // 获取图片
        requestHttpHeaders = new HttpHeaders();
        for (Map.Entry entry : cookies.entrySet()) {
            requestHttpHeaders.add(HttpHeaders.COOKIE, entry.getKey() + "=" + entry.getValue());
        }
        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&0.23335437505509327",
                HttpMethod.GET,
                new HttpEntity<>(requestHttpHeaders),
                byte[].class
        );
        responseHttpHeaders = response.getHeaders();
        setCookies = responseHttpHeaders.get("Set-Cookie");
        for (String string : setCookies) {
            String[] s = string.split(";")[0].split("=");
            cookies.put(s[0], s[1]);
        }

        // 创建要保存图片的文件夹
        String folderName = DateUtils.getFolderName(new Date());
        File folder = new File(customConfig.getForder12306() + "/" + folderName);
        folder.mkdir();

        // 将全图保存到文件夹中
        File jpegFile = new File(folder, PictureNames.FULL);
        byte[] bytes = response.getBody();
        try (FileOutputStream out = new FileOutputStream(jpegFile)) {
            out.write(bytes);
        } catch (Exception e) {
            log.error("e = {}", e);
            throw e;
        }

        // 获取问题图片
        BufferedImage questionImage = JpgUtils.getQuestionImage(jpegFile);

        // 获取子图
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 2; y++) {
                JpgUtils.writeSubImage(jpegFile, x, y);
            }
        }

        // 构造需要返回的结构体
        QuestionInfoVO questionInfoVO = new QuestionInfoVO();
        questionInfoVO.setFolderName(folderName);
        questionInfoVO.setQuestionUrl(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.QUESTION);
        questionInfoVO.setQuestion(JpgUtils.getJpegQuestion(baiduTokenVO, restTemplate, questionImage));
        questionInfoVO.setPic00Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC00);
        questionInfoVO.setPic01Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC01);
        questionInfoVO.setPic02Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC02);
        questionInfoVO.setPic03Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC03);
        questionInfoVO.setPic10Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC10);
        questionInfoVO.setPic11Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC11);
        questionInfoVO.setPic12Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC12);
        questionInfoVO.setPic13Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC13);

        // 把之前的信息写入到session中，以便后续登录
        httpSession.setAttribute("cookies", cookies);

        return ResultUtils.success(questionInfoVO);
    }

    @PostMapping("/answerQuestion")
    @ResponseBody
    public ResultVO answerQuestion(QuestionInfoVO questionInfoVO, HttpSession httpSession) throws Exception {

        // 出错检查
        if (StringUtils.isEmpty(questionInfoVO.getQuestion())) {
            return ResultUtils.error(ResultEnum.QUESTION_IS_EMPTY);
        } else if (StringUtils.isEmpty(questionInfoVO.getChoose())) {
            return ResultUtils.error(ResultEnum.ANSWER_IS_EMPTY);
        } else if (StringUtils.isEmpty(questionInfoVO.getFolderName())) {
            return ResultUtils.error(ResultEnum.FOLDER_NAME_IS_EMPTY);
        }

        // 获取选中的图片，然后尝试向12306发送登录请求
        List<Integer> answerList = new ArrayList<>();
        String[] chooses = questionInfoVO.getChoose().split(",");
        for (String choose : chooses) {
            int chos = Integer.valueOf(choose);
            int x = (chos - 1) % 4;
            int y = (chos - 1) / 4;
            Point point = JpgUtils.getSubImageChoosePoint(x, y);
            answerList.add(point.x);
            answerList.add(point.y);
        }

        // 从session中获取cookie
        Map<String, Object> cookies = (Map<String, Object>) httpSession.getAttribute("cookies");

        // 带上cookie发送图片选择信息
        HttpHeaders requestHttpHeaders = new HttpHeaders();
        for (Map.Entry entry : cookies.entrySet()) {
            requestHttpHeaders.add(HttpHeaders.COOKIE, entry.getKey() + "=" + entry.getValue());
        }
        requestHttpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("answer", StringUtils.join(answerList, ","));
        map.add("login_site", "E");
        map.add("rand", "sjrand");
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://kyfw.12306.cn/passport/captcha/captcha-check",
                HttpMethod.POST,
                new HttpEntity<>(map, requestHttpHeaders),
                String.class
        );
        log.debug("response={}", responseEntity.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        if (jsonNode.get("result_code").textValue().equals("4")) {

            // 执行登录操作
            requestHttpHeaders = new HttpHeaders();
            for (Map.Entry entry : cookies.entrySet()) {
                requestHttpHeaders.add(HttpHeaders.COOKIE, entry.getKey() + "=" + entry.getValue());
            }
            requestHttpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            map = new LinkedMultiValueMap<>();
            map.add("username", "nbda1121440");
            map.add("password", "Zjf@1234");
            map.add("appid", "otn");
            responseEntity = restTemplate.exchange(
                    "https://kyfw.12306.cn/passport/web/login",
                    HttpMethod.POST,
                    new HttpEntity<>(map, requestHttpHeaders),
                    String.class
            );
            log.debug("response={}", responseEntity.getBody());
            jsonNode = objectMapper.readTree(responseEntity.getBody());
            if (jsonNode.get("result_code").intValue() == 0) {
                // 将结果写入数据库中
                VerificationCode verificationCode = new VerificationCode();
                verificationCode.setFolderName(questionInfoVO.getFolderName());
                verificationCode.setQuestion(questionInfoVO.getQuestion());
                verificationCode.setChoose(questionInfoVO.getChoose());
                for (String choose : chooses) {
                    if (choose.equals("1")) {
                        verificationCode.setPic00Desc(questionInfoVO.getQuestion());
                    } else if (choose.equals("2")) {
                        verificationCode.setPic01Desc(questionInfoVO.getQuestion());
                    } else if (choose.equals("3")) {
                        verificationCode.setPic02Desc(questionInfoVO.getQuestion());
                    } else if (choose.equals("4")) {
                        verificationCode.setPic03Desc(questionInfoVO.getQuestion());
                    } else if (choose.equals("5")) {
                        verificationCode.setPic10Desc(questionInfoVO.getQuestion());
                    } else if (choose.equals("6")) {
                        verificationCode.setPic11Desc(questionInfoVO.getQuestion());
                    } else if (choose.equals("7")) {
                        verificationCode.setPic12Desc(questionInfoVO.getQuestion());
                    } else if (choose.equals("8")) {
                        verificationCode.setPic13Desc(questionInfoVO.getQuestion());
                    }
                }
                verificationCodeRepository.save(verificationCode);

                return getUncheckQuestion(httpSession);
            } else {
                return ResultUtils.error(ResultEnum.LOGIN_ERROR);
            }
        } else {
            // 执行失败操作
            return ResultUtils.error(ResultEnum.ANSWER_ERROR);
        }
    }
}
