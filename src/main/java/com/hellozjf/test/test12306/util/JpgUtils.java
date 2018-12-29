package com.hellozjf.test.test12306.util;

import com.google.common.collect.ImmutableMap;
import com.hellozjf.test.test12306.constant.PictureNames;
import com.hellozjf.test.test12306.vo.BaiduTokenVO;
import com.hellozjf.test.test12306.vo.OrcResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
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
    public static String getJpegQuestion(BaiduTokenVO baiduTokenVO, RestTemplate restTemplate, File jpegFile) throws Exception {

        // 获取问题图片
        BufferedImage subImage = JpgUtils.getQuestionImage(jpegFile);
        return getJpegQuestion(baiduTokenVO, restTemplate, subImage);
    }

    /**
     * 获取验证码图片中的问题
     *
     * @param bufferedImage 要解析的BufferedImage
     * @return
     * @throws Exception
     */
    public static String getJpegQuestion(BaiduTokenVO baiduTokenVO, RestTemplate restTemplate, BufferedImage bufferedImage) throws Exception {

        // 识别文字
        String url = String.format("https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token=%s",
                baiduTokenVO.getAccessToken());
        HttpEntity httpEntity = HttpEntityUtils.getHttpEntity(MediaType.APPLICATION_FORM_URLENCODED, ImmutableMap.of(
                "image", changeJpegToBase64(bufferedImage)
        ));
        OrcResultVO orcResultVO = restTemplate.postForObject(url, httpEntity, OrcResultVO.class);
        if (orcResultVO.getWordsResultNum() == null || orcResultVO.getWordsResultNum() == 0) {
            log.error("wordNum = {}, maybe api count limit reached", orcResultVO.getWordsResultNum());
            return "";
        }
        return orcResultVO.getWordsResult().get(0).getWords();
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
