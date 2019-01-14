package com.hellozjf.test.test12306.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *MD5计算工具 xuxile 2017-09-13
 */
@Slf4j
public class Md5CaculateUtils {

    /**
     * 获取一个文件的md5值(可处理大文件)
     * @return md5 value
     */
    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 求一个字符串的md5值
     * @param target 字符串
     * @return md5 value
     */
    public static String MD5(String target) {
        return DigestUtils.md5Hex(target);
    }

//    public static void main(String[] args) {
//        File file1 = new File("C:\\Users\\Administrator\\AppData\\Local\\Temp\\18acf16ee1ad46fb81f0eac8934c143c\\安全帽\\20181231103224131_pic00.jpg");
//        File file2 = new File("C:\\Users\\Administrator\\AppData\\Local\\Temp\\18acf16ee1ad46fb81f0eac8934c143c\\安全帽\\20181231103224131_pic03.jpg");
//        File file3 = new File("C:\\Users\\Administrator\\AppData\\Local\\Temp\\18acf16ee1ad46fb81f0eac8934c143c\\安全帽\\1.jpg");
//        log.debug("file1={}", getMD5(file1));
//        log.debug("file2={}", getMD5(file2));
//        log.debug("file3={}", getMD5(file3));
//    }
}
