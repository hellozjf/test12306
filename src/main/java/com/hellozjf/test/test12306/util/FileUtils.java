package com.hellozjf.test.test12306.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class FileUtils {

    /**
     * 将url的图片下载到本地
     * @param urlPath
     * @return
     * @throws Exception
     */
    public static File downloadFile(String urlPath, String filePath) throws Exception {

        URL url = new URL(urlPath);
        File file = new File(filePath);

        try (
                DataInputStream dataInputStream = new DataInputStream(url.openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(file)
        ) {
            IOUtils.copy(dataInputStream, fileOutputStream);
        } catch (Exception e) {
            throw e;
        }

        return file;
    }
}
