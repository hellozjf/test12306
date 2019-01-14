package com.hellozjf.test.test12306.controller;

import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.constant.DisposeResultEnums;
import com.hellozjf.test.test12306.constant.PictureNames;
import com.hellozjf.test.test12306.dataobject.VerificationCode;
import com.hellozjf.test.test12306.repository.VerificationCodeRepository;
import com.hellozjf.test.test12306.util.ResultUtils;
import com.hellozjf.test.test12306.util.UUIDUtils;
import com.hellozjf.test.test12306.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author hellozjf
 */
@Controller
@RequestMapping("/download")
@Slf4j
public class DownloadController {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private CustomConfig customConfig;

    /**
     * 下载每种问题对应的问题文字图片
     *
     * @return
     */
    @GetMapping("/questionWords")
    @ResponseBody
//    public ResponseEntity<byte[]> downloadQuestionWords() throws Exception {
    public ResultVO downloadQuestionWords() throws Exception {

        // 首先查出来所有的问题种类
        List<String> questionList = verificationCodeRepository.findByDisposeResultEqualsGroupByQuestion(DisposeResultEnums.MANUAL_DISPOSE.getCode());
        log.debug("questionList = {}", questionList);

        // 在java.io.tmpdir下面用UUID新建一个文件夹，该文件夹下面会有各个问题种类文件夹，然后在问题种类文件夹下面会有各个问题种类的问题文字图片
        File tmpFolder = new File(System.getProperty("java.io.tmpdir"), UUIDUtils.genId());
        tmpFolder.mkdir();

        // 然后将这些种类对应的图片都下载下来
        for (String question : questionList) {
            // 在tmpFolder下面新建问题种类文件夹
            File questionFolder = new File(tmpFolder, question);
            questionFolder.mkdir();

            // 获取所有和该问题相关的VerificationCode
            List<VerificationCode> verificationCodeList = verificationCodeRepository.findByDisposeResultEqualsAndQuestionEquals(DisposeResultEnums.MANUAL_DISPOSE.getCode(), question);
            if (verificationCodeList.size() == 0) {
                log.warn("question={} size=0", question);
                continue;
            }

            // 判断本地有没有图片，有的话直接从本地拷贝文件，否则从网络拷贝文件
            File folder = new File(customConfig.getForder12306() + "/" + verificationCodeList.get(0).getQuestion());
            if (folder.exists()) {
                // TODO 这个逻辑等下再写
            } else {
                for (VerificationCode verificationCode : verificationCodeList) {
                    // 要保存的文件
                    File file = new File(questionFolder, verificationCode.getFolderName() + ".jpg");
//                    log.debug("download {}", file.getAbsolutePath());
                    URL url = new URL(customConfig.getNetPrefix() + "/" + verificationCode.getFolderName() + "/" + PictureNames.QUESTION);

                    // 把网络图片拷贝到本地来
                    BufferedImage img = ImageIO.read(url);
//                    log.debug("file={} width={} height={}", file.getName(), img.getWidth(), img.getHeight());

                    // 只有当图片在问题文件夹中不存在时，才保存
                    if (! repetition(questionFolder, img)) {
                        ImageIO.write(img, "jpg", file);
                    }
                }
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "test.txt");

//        return new ResponseEntity<byte[]>(null,
//                headers, HttpStatus.CREATED);
        return ResultUtils.success();
    }

    /**
     * 判断文件夹中有没有相同的图片
     * @param questionFolder
     * @param img
     * @return
     */
    private boolean repetition(File questionFolder, BufferedImage img) {
        try {
            File[] files = questionFolder.listFiles();
            for (File file : files) {
                BufferedImage bufferedImage = ImageIO.read(file);

                ByteArrayOutputStream origin = new ByteArrayOutputStream();
                ByteArrayOutputStream target = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", origin);
                ImageIO.write(img, "jpg", target);
//                log.debug("originFileName:{}", file.getName());
//                log.debug("origin:{}", Arrays.toString(origin.toByteArray()));
//                log.debug("target:{}", Arrays.toString(target.toByteArray()));
                if (Arrays.equals(origin.toByteArray(), target.toByteArray())) {
                    log.debug("equals");
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            log.error("e = {}", e);
            return true;
        }
    }

    /**
     * 下载每种问题对应的问题文字图片
     *
     * @return
     */
    @GetMapping("/answerPictures")
    @ResponseBody
//    public ResponseEntity<byte[]> downloadQuestionWords() throws Exception {
    public ResultVO answerPictures() throws Exception {

        // 首先查出来所有的问题种类
        List<String> questionList = verificationCodeRepository.findByDisposeResultEqualsGroupByQuestion(DisposeResultEnums.MANUAL_DISPOSE.getCode());
        log.debug("questionList = {}", questionList);

        // 在java.io.tmpdir下面用UUID新建一个文件夹，该文件夹下面会有各个问题种类文件夹，然后在问题种类文件夹下面会有各个问题种类的问题文字图片
        File tmpFolder = new File(System.getProperty("java.io.tmpdir"), UUIDUtils.genId());
        tmpFolder.mkdir();

        // 然后将这些种类对应的图片都下载下来
        for (String question : questionList) {
            // 在tmpFolder下面新建问题种类文件夹
            File questionFolder = new File(tmpFolder, question);
            questionFolder.mkdir();

            // 获取所有和该问题相关的VerificationCode
            List<VerificationCode> verificationCodeList = verificationCodeRepository.findByDisposeResultEqualsAndQuestionEquals(DisposeResultEnums.MANUAL_DISPOSE.getCode(), question);
            if (verificationCodeList.size() == 0) {
                log.warn("question={} size=0", question);
                continue;
            }

            // 判断本地有没有图片，有的话直接从本地拷贝文件，否则从网络拷贝文件
            File folder = new File(customConfig.getForder12306() + "/" + verificationCodeList.get(0).getQuestion());
            if (folder.exists()) {
                // TODO 这个逻辑等下再写
            } else {
                for (VerificationCode verificationCode : verificationCodeList) {

                    String chooseString = verificationCode.getChoose();
                    String[] chooses = chooseString.split(",");

                    for (String choose : chooses) {

                        String picName = "";
                        switch (choose) {
                            case "1":
                                picName = PictureNames.PIC00;
                                break;
                            case "2":
                                picName = PictureNames.PIC01;
                                break;
                            case "3":
                                picName = PictureNames.PIC02;
                                break;
                            case "4":
                                picName = PictureNames.PIC03;
                                break;
                            case "5":
                                picName = PictureNames.PIC10;
                                break;
                            case "6":
                                picName = PictureNames.PIC11;
                                break;
                            case "7":
                                picName = PictureNames.PIC12;
                                break;
                            case "8":
                                picName = PictureNames.PIC13;
                                break;
                        }

                        // 如果没有图片名称，那对不起，这是有问题的数据，报错吧，骚年
                        if (picName.equals("")) {
                            log.error("unknown choose {}", choose);
                            continue;
                        }

                        // 要保存的文件
                        File file = new File(questionFolder, verificationCode.getFolderName() + "_" + picName);
//                        log.debug("download {}", file.getAbsolutePath());
                        URL url = new URL(customConfig.getNetPrefix() + "/" + verificationCode.getFolderName() + "/" + picName);

                        // 把网络图片拷贝到本地来
                        BufferedImage img = ImageIO.read(url);
//                        log.debug("file={} width={} height={}", file.getName(), img.getWidth(), img.getHeight());

                        // 只有当图片在问题文件夹中不存在时，才保存
                        if (! repetition(questionFolder, img)) {
                            ImageIO.write(img, "jpg", file);
                        }
                    }
                }
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "test.txt");

//        return new ResponseEntity<byte[]>(null,
//                headers, HttpStatus.CREATED);
        return ResultUtils.success();
    }
}

