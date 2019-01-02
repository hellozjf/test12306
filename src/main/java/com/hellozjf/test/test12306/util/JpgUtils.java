package com.hellozjf.test.test12306.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.constant.PictureNames;
import com.hellozjf.test.test12306.vo.BaiduTokenVO;
import com.hellozjf.test.test12306.vo.OrcResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class JpgUtils {

    /**
     * 获取问题图片，并保存在源文件同一目录
     * @param jpegFile
     * @return
     * @throws Exception
     */
    public static BufferedImage getQuestionImage(File jpegFile) throws Exception {

        BufferedImage bufImage = ImageIO.read(jpegFile);

        // 获取右上角的文字信息
        BufferedImage subImage = bufImage.getSubimage(116, 0, 48 * 2, 30);
        ImageIO.write(subImage, "JPEG", new File(jpegFile.getParent(), PictureNames.QUESTION));

        return subImage;
    }

    /**
     * 获取子图片，并保存在源文件同一目录
     *
     * @param jpegFile
     * @param x
     * @param y
     * @return
     * @throws Exception
     */
    public static BufferedImage writeSubImage(File jpegFile, int x, int y) throws Exception {
        BufferedImage bufImage = ImageIO.read(jpegFile);
        Point startPoint = getSubImageStartPoint(x, y);
        int size = getSubImageSize();
        BufferedImage subImage = bufImage.getSubimage(startPoint.x, startPoint.y, size, size);
        ImageIO.write(subImage, "JPEG", new File(jpegFile.getParent(), PictureNames.PIC_PREFIX + y + x + PictureNames.JPG_SUFFIX));
        return subImage;
    }



    /**
     * 获取验证码图片中的问题
     *
     * @param jpegFile 要解析的jpg文件
     * @return
     * @throws Exception
     */
    public static String getJpegQuestion(BaiduTokenVO baiduTokenVO, RestTemplate restTemplate, File jpegFile,
                                         ObjectMapper objectMapper, CustomConfig customConfig) throws Exception {

        // 获取问题图片
        BufferedImage subImage = JpgUtils.getQuestionImage(jpegFile);
        return getJpegQuestion(baiduTokenVO, restTemplate, subImage, objectMapper, customConfig);
    }

    /**
     * 先用精确文字解析，再使用一般文字解析
     * @param baiduTokenVO
     * @param restTemplate
     * @param bufferedImage
     * @param objectMapper
     * @param customConfig
     * @return
     * @throws Exception
     */
    public static String getJpegQuestion(BaiduTokenVO baiduTokenVO, RestTemplate restTemplate, BufferedImage bufferedImage,
                                         ObjectMapper objectMapper, CustomConfig customConfig) throws Exception {
        // 如果能使用精确文字解析，那就使用精确文字解析
        String accurateString = getJpegQuestion(baiduTokenVO, restTemplate, bufferedImage,
                objectMapper, customConfig, "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic");
        if (!StringUtils.isEmpty(accurateString)) {
            return accurateString;
        }
        // 否则使用一般文字解析
        String generalString = getJpegQuestion(baiduTokenVO, restTemplate, bufferedImage,
                objectMapper, customConfig, "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic");
        return generalString;
    }

    /**
     * 使用给定的API来获取验证码图片中的问题
     *
     * @param bufferedImage 要解析的BufferedImage
     * @return
     * @throws Exception
     */
    public static String getJpegQuestion(BaiduTokenVO baiduTokenVO, RestTemplate restTemplate, BufferedImage bufferedImage,
                                         ObjectMapper objectMapper, CustomConfig customConfig, String urlNoParam) throws Exception {
        // 识别文字
        String url = String.format(urlNoParam + "?access_token=%s", baiduTokenVO.getAccessToken());
        HttpEntity httpEntity = HttpEntityUtils.getHttpEntity(MediaType.APPLICATION_FORM_URLENCODED, ImmutableMap.of(
                "image", changeJpegToBase64(bufferedImage)
        ));

        // 获取结果
        JsonNode jsonNode = restTemplate.postForObject(url, httpEntity, JsonNode.class);
        if (jsonNode.get("error_code") == null) {
            OrcResultVO orcResultVO = objectMapper.treeToValue(jsonNode, OrcResultVO.class);
            if (orcResultVO.getWordsResultNum() > 0) {
                String word = orcResultVO.getWordsResult().get(0).getWords();
                log.debug("{} get {}", urlNoParam, word);
                return word;
            } else {
                log.debug("wordsResultNum == 0");
                return "";
            }
        } else {
            // 需要判断出现这种情况是因为免费次数用光了，或者token失效了
            int errorCode = jsonNode.get("error_code").intValue();
            if (errorCode == 110) {
                // token失效，那就获取新的token
                BaiduTokenVO bt = BaiduAIUtils.getBaiduTokenVO(customConfig, restTemplate);
                log.info("invalid token {}, then using new token {}", baiduTokenVO.getAccessToken(), bt.getAccessToken());
                BeanUtils.copyProperties(bt, baiduTokenVO);
                // 重新获取一遍
                return getJpegQuestion(baiduTokenVO, restTemplate, bufferedImage, objectMapper, customConfig, urlNoParam);
            } else if (errorCode == 17) {
                // 说明免费次数用光了
                log.debug("{} limit reached", urlNoParam);
                return "";
            } else {
                log.error("unknown error, errorCode={}", errorCode);
                return "";
            }
        }

    }

    /**
     * 将JPEG图片转化为base64编码字符串
     * @param bufferedImage
     * @return
     * @throws Exception
     */
    public static String changeJpegToBase64(BufferedImage bufferedImage) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        String base64Img = Base64.encodeBase64String(outputStream.toByteArray());
        return base64Img;
    }

    /**
     * 获取子图对应的起始点位置
     * @return
     */
    public static Point getSubImageStartPoint(int x, int y) {
        int left = 5 + (67 + 5) * x;
        int top = 41 + (67 + 5) * y;
        return new Point(left, top);
    }

    /**
     * 获取子图的大小，但是它的长和宽都是一样大的，都是67
     * @return
     */
    public static Integer getSubImageSize() {
        return 67;
    }

    /**
     * 获取子图选择的点
     * @param x
     * @param y
     * @return
     */
    public static Point getSubImageChoosePoint(int x, int y) {
        Point point = getSubImageStartPoint(x, y);
        point.x += getSubImageSize() / 2;
        point.y += getSubImageSize() / 2;
        return point;
    }
}
