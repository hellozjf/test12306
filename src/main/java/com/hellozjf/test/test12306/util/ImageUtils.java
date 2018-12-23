package com.hellozjf.test.test12306.util;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * @author hellozjf
 */
public class ImageUtils {

    /**
     * 将JPEG图片转化为base64编码字符串
     * @param bufferedImage
     * @return
     * @throws Exception
     */
    public static String changeJpegToBase64(BufferedImage bufferedImage) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Img = encoder.encode(outputStream.toByteArray());
        return base64Img;
    }
}
