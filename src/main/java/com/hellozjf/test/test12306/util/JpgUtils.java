package com.hellozjf.test.test12306.util;

import com.hellozjf.test.test12306.constant.PictureNames;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author Jingfeng Zhou
 */
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
        BufferedImage subImage = bufImage.getSubimage(119, 0, 47 * 2, 30);
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
        int left = 5 + (67 + 5) * x;
        int top = 41 + (67 + 5) * y;
        BufferedImage subImage = bufImage.getSubimage(left, top, 67, 67);
        ImageIO.write(subImage, "JPEG", new File(jpegFile.getParent(), PictureNames.PIC_PREFIX + y + x + PictureNames.JPG_SUFFIX));
        return subImage;
    }
}
