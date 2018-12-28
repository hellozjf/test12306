package com.hellozjf.test.test12306.controller;

import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.constant.PictureNames;
import com.hellozjf.test.test12306.constant.ResultEnum;
import com.hellozjf.test.test12306.dataobject.VerificationCode;
import com.hellozjf.test.test12306.repository.VerificationCodeRepository;
import com.hellozjf.test.test12306.util.DateUtils;
import com.hellozjf.test.test12306.util.HttpHeaderUtils;
import com.hellozjf.test.test12306.util.JpgUtils;
import com.hellozjf.test.test12306.util.ResultUtils;
import com.hellozjf.test.test12306.vo.QuestionInfoVO;
import com.hellozjf.test.test12306.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String prefix = PictureNames.NET_PRIFIX;
        questionInfoVO.setQuestionUrl(prefix + folderName + "/question.jpg");
        questionInfoVO.setPic00Url(prefix + folderName + "/pic00.jpg");
        questionInfoVO.setPic01Url(prefix + folderName + "/pic01.jpg");
        questionInfoVO.setPic02Url(prefix + folderName + "/pic02.jpg");
        questionInfoVO.setPic03Url(prefix + folderName + "/pic03.jpg");
        questionInfoVO.setPic10Url(prefix + folderName + "/pic10.jpg");
        questionInfoVO.setPic11Url(prefix + folderName + "/pic11.jpg");
        questionInfoVO.setPic12Url(prefix + folderName + "/pic12.jpg");
        questionInfoVO.setPic13Url(prefix + folderName + "/pic13.jpg");
        return questionInfoVO;
    }

    @GetMapping("/getUncheckQuestion")
    @ResponseBody
    public ResultVO getUncheckQuestion(HttpSession httpSession) throws Exception {

        Map<String, Object> cookies = new HashMap<>();

        // 通过login获取jsessionid，然后通过这个jsessionid去获取图片
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://kyfw.12306.cn/otn/login/init", String.class);
        HttpHeaders resHttpHeaders = responseEntity.getHeaders();
        List<String> setCookies = resHttpHeaders.get(HttpHeaders.SET_COOKIE);

        // 访问https://kyfw.12306.cn/passport/web/auth/uamtk
        for (String string : setCookies) {
            String[] s = string.split(";")[0].split("=");
            cookies.put(s[0], s[1]);
        }
        HttpHeaders reqHttpHeaders = new HttpHeaders();
        for (Map.Entry entry : cookies.entrySet()) {
            reqHttpHeaders.add(HttpHeaders.COOKIE, entry.getKey() + "=" + entry.getValue());
        }
        reqHttpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("appid", "otn");
        responseEntity = restTemplate.exchange(
                "https://kyfw.12306.cn/passport/web/auth/uamtk",
                HttpMethod.POST,
                new HttpEntity<>(map, reqHttpHeaders),
                String.class
        );
        resHttpHeaders = responseEntity.getHeaders();
        setCookies = resHttpHeaders.get("Set-Cookie");
        for (String string : setCookies) {
            String[] s = string.split(";")[0].split("=");
            cookies.put(s[0], s[1]);
        }

        // 获取图片
        reqHttpHeaders = new HttpHeaders();
        for (Map.Entry entry : cookies.entrySet()) {
            reqHttpHeaders.add(HttpHeaders.COOKIE, entry.getKey() + "=" + entry.getValue());
        }
        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&0.23335437505509327",
                HttpMethod.GET,
                new HttpEntity<>(reqHttpHeaders),
                byte[].class
        );
        resHttpHeaders = responseEntity.getHeaders();
        setCookies = resHttpHeaders.get("Set-Cookie");
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
        JpgUtils.getQuestionImage(jpegFile);

        // 获取子图
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 2; y++) {
                JpgUtils.writeSubImage(jpegFile, x, y);
            }
        }

        // 构造需要返回的结构体
        QuestionInfoVO questionInfoVO = new QuestionInfoVO();
        questionInfoVO.setFolderName(folderName);
        questionInfoVO.setQuestionUrl(PictureNames.NET_PRIFIX + folderName + "/" + PictureNames.QUESTION);
        questionInfoVO.setPic00Url(PictureNames.NET_PRIFIX + folderName + "/" + PictureNames.PIC00);
        questionInfoVO.setPic01Url(PictureNames.NET_PRIFIX + folderName + "/" + PictureNames.PIC01);
        questionInfoVO.setPic02Url(PictureNames.NET_PRIFIX + folderName + "/" + PictureNames.PIC02);
        questionInfoVO.setPic03Url(PictureNames.NET_PRIFIX + folderName + "/" + PictureNames.PIC03);
        questionInfoVO.setPic10Url(PictureNames.NET_PRIFIX + folderName + "/" + PictureNames.PIC10);
        questionInfoVO.setPic11Url(PictureNames.NET_PRIFIX + folderName + "/" + PictureNames.PIC11);
        questionInfoVO.setPic12Url(PictureNames.NET_PRIFIX + folderName + "/" + PictureNames.PIC12);
        questionInfoVO.setPic13Url(PictureNames.NET_PRIFIX + folderName + "/" + PictureNames.PIC13);

        // 把之前的信息写入到session中，以便后续登录
        httpSession.setAttribute("cookies", cookies);

        return ResultUtils.success(questionInfoVO);
    }

    @PostMapping("/answerQuestion")
    @ResponseBody
    public ResultVO answerQuestion(QuestionInfoVO questionInfoVO) {

        // 出错检查
        if (StringUtils.isEmpty(questionInfoVO.getQuestion())) {
            return ResultUtils.error(ResultEnum.QUESTION_IS_EMPTY);
        } else if (StringUtils.isEmpty(questionInfoVO.getChoose())) {
            return ResultUtils.error(ResultEnum.ANSWER_IS_EMPTY);
        } else if (StringUtils.isEmpty(questionInfoVO.getFolderName())) {
            return ResultUtils.error(ResultEnum.FOLDER_NAME_IS_EMPTY);
        }

        //

        // 更新数据库
        VerificationCode verificationCode = verificationCodeRepository.findByFolderName(questionInfoVO.getFolderName());
        verificationCode.setChoose(questionInfoVO.getChoose());
        verificationCode.setQuestion(questionInfoVO.getQuestion());
        verificationCodeRepository.save(verificationCode);

        QuestionInfoVO nextQuestionInfoVO = getNextUncheckQuestion();
        return ResultUtils.success(nextQuestionInfoVO);
    }
}
