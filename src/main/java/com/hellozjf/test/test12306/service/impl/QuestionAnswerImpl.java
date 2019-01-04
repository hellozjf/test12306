package com.hellozjf.test.test12306.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.constant.DisposeResultEnums;
import com.hellozjf.test.test12306.constant.PictureNames;
import com.hellozjf.test.test12306.constant.ResultEnum;
import com.hellozjf.test.test12306.dataobject.VerificationCode;
import com.hellozjf.test.test12306.repository.VerificationCodeRepository;
import com.hellozjf.test.test12306.service.IQuestionAnswer;
import com.hellozjf.test.test12306.service.IService12306;
import com.hellozjf.test.test12306.util.DateUtils;
import com.hellozjf.test.test12306.util.JpgUtils;
import com.hellozjf.test.test12306.util.ResultUtils;
import com.hellozjf.test.test12306.vo.BaiduTokenVO;
import com.hellozjf.test.test12306.vo.QuestionInfoVO;
import com.hellozjf.test.test12306.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class QuestionAnswerImpl implements IQuestionAnswer {

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

    @Autowired
    private IService12306 service12306;

    @Override
    public ResultVO getQuestion(HttpSession httpSession) {
        Map<String, Object> cookies = new HashMap<>();

        // 访问https://kyfw.12306.cn/otn/login/init
        do {
            ResponseEntity<String> responseEntity = service12306.otnLoginInit();
            List<String> setCookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookies != null) {
                service12306.setCookies(cookies, setCookies);
                log.debug("finish /otn/login/init");
                break;
            } else {
                log.error("setCookies == null");
            }
        } while (true);

        // 访问https://kyfw.12306.cn/passport/web/auth/uamtk
        do {
            ResponseEntity<String> responseEntity = service12306.webAuthUamtk(cookies);
            List<String> setCookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookies != null) {
                service12306.setCookies(cookies, setCookies);
                log.debug("finish /passport/web/auth/uamtk");
                break;
            } else {
                log.error("setCookies == null");
            }
        } while (true);

        File jpegFile = null;
        String folderName = null;
        BufferedImage questionImage = null;
        do {
            // 获取图片
            ResponseEntity<byte[]> response = service12306.passportCaptchaCaptchaImage(cookies);
            List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookies != null) {
                service12306.setCookies(cookies, setCookies);
                log.debug("finish /passport/captcha/captcha-image");

                // 创建要保存图片的文件夹
                File folder = null;
                try {
                    folderName = DateUtils.getFolderName(new Date());
                    folder = new File(customConfig.getForder12306(), folderName);
                    folder.mkdirs();
                } catch (Exception e) {
                    log.error("e = {}", e);
                    return ResultUtils.error(ResultEnum.CREATE_FOLDER_ERROR);
                }

                // 将全图保存到文件夹中
                try {
                    jpegFile = JpgUtils.saveFullJpeg(folder, response.getBody());
                } catch (IOException e) {
                    log.error("e = {}", e);
                    return ResultUtils.error(ResultEnum.WRITE_FILE_ERROR);
                }

                // 获取题目图片，并写到当前文件夹的question.jpg中
                try {
                    questionImage = JpgUtils.getQuestionImage(jpegFile);
                } catch (Exception e) {
                    // 这里可能会失败，失败就再次获取图片
                    // 这里失败有可能是因为编码格式不正确导致的，经常会出现0x22不是jpeg这样的错误
                    log.error("e = {}", e);
                    continue;
                }
                break;
            } else {
                log.error("setCookies == null");
            }
        } while (true);

        // 获取子图
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 2; y++) {
                try {
                    JpgUtils.writeSubImage(jpegFile, x, y);
                } catch (Exception e) {
                    log.error("e = {}", e);
                    return ResultUtils.error(ResultEnum.WRITE_FILE_ERROR);
                }
            }
        }

        // 返回获取问题的结果
        return getGetQuestionResultVO(httpSession, cookies, jpegFile, folderName, questionImage);
    }

    @Override
    public ResultVO answerQuestion(QuestionInfoVO questionInfoVO, HttpSession httpSession) {
        // 出错检查
        if (StringUtils.isEmpty(questionInfoVO.getChoose())) {
            return ResultUtils.error(ResultEnum.ANSWER_IS_EMPTY);
        } else if (StringUtils.isEmpty(questionInfoVO.getFolderName())) {
            return ResultUtils.error(ResultEnum.FOLDER_NAME_IS_EMPTY);
        }

        // 获取选中的图片，然后尝试向12306发送登录请求
        String[] chooses = questionInfoVO.getChoose().split(",");
        List<Integer> answerList = getAnswerList(chooses);

        // 从session中获取cookie
        Map<String, Object> cookies = (Map<String, Object>) httpSession.getAttribute(HttpHeaders.COOKIE);

        // 带上cookie发送图片选择信息
        ResponseEntity<String> responseEntity = service12306.passportCaptchaCaptchaCheck(answerList, cookies);
        log.debug("response={}", responseEntity.getBody());

        // 将选择图片的结果放到一个JsonNode中，这样可以通过JsonNode.get方法判断结果是否正确
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseEntity.getBody());
        } catch (IOException e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.UNKNOWN_ERROR.getCode(),
                    ResultEnum.UNKNOWN_ERROR.getMessage() + "\n" + e.getMessage());
        }

        if (jsonNode.get("result_code").textValue().equals("4")) {
            // 结果为4，表示正确，那就将结果写入数据库中
            saveRightAnswer(questionInfoVO, chooses);
            return ResultUtils.success();
        } else {
            // 执行失败操作
            return ResultUtils.error(ResultEnum.ANSWER_ERROR);
        }
    }

    /**
     * 将正确的结果保存到数据库中
     * @param questionInfoVO
     * @param chooses
     */
    private void saveRightAnswer(QuestionInfoVO questionInfoVO, String[] chooses) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setFolderName(questionInfoVO.getFolderName());
        verificationCode.setQuestion(questionInfoVO.getQuestion());
        verificationCode.setDisposeResult(DisposeResultEnums.UNDISPOSE.getCode());
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
    }

    /**
     * 构造12306回答所需的数字列表
     * @param chooses
     * @return
     */
    private List<Integer> getAnswerList(String[] chooses) {
        List<Integer> answerList = new ArrayList<>();
        for (String choose : chooses) {
            int chos = Integer.valueOf(choose);
            int x = (chos - 1) % 4;
            int y = (chos - 1) / 4;
            Point point = JpgUtils.getSubImageChoosePoint(x, y);
            answerList.add(point.x);
            answerList.add(point.y);
        }
        return answerList;
    }

    /**
     * 获取问题时的答复
     * @param httpSession
     * @param cookies
     * @param jpegFile
     * @param folderName
     * @param questionImage
     * @return
     */
    private ResultVO getGetQuestionResultVO(HttpSession httpSession, Map<String, Object> cookies, File jpegFile, String folderName, BufferedImage questionImage) {
        // 构造需要返回的结构体
        QuestionInfoVO questionInfoVO = new QuestionInfoVO();
        questionInfoVO.setFolderName(folderName);
        questionInfoVO.setQuestionUrl(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.QUESTION);
        String jpegQuestion = null;
        try {
            jpegQuestion = JpgUtils.getJpegWords(baiduTokenVO, restTemplate, questionImage, objectMapper, customConfig);
            if (jpegQuestion.equals("")) {
                // 这里有可能是刷新太快了，或者12306关闭了服务，所以我们要拿整张图去进行查询，看是不是刷新太快了
                BufferedImage bufImage = ImageIO.read(jpegFile);
                String words = JpgUtils.getJpegWords(baiduTokenVO, restTemplate, bufImage, objectMapper, customConfig);
                if (words.indexOf("刷新的频率") != -1) {
                    return ResultUtils.error(ResultEnum.REFRESH_TOO_FAST);
                }
            }
        } catch (Exception e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.UNKNOWN_ERROR.getCode(),
                    ResultEnum.UNKNOWN_ERROR.getMessage() + "\n" + e.getMessage());
        }
        questionInfoVO.setQuestion(jpegQuestion);
        questionInfoVO.setPic00Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC00);
        questionInfoVO.setPic01Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC01);
        questionInfoVO.setPic02Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC02);
        questionInfoVO.setPic03Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC03);
        questionInfoVO.setPic10Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC10);
        questionInfoVO.setPic11Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC11);
        questionInfoVO.setPic12Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC12);
        questionInfoVO.setPic13Url(customConfig.getNetPrefix() + "/" + folderName + "/" + PictureNames.PIC13);

        // 把之前的信息写入到session中，以便后续登录
        httpSession.setAttribute(HttpHeaders.COOKIE, cookies);

        return ResultUtils.success(questionInfoVO);
    }
}
