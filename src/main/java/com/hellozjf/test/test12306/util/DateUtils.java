package com.hellozjf.test.test12306.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jingfeng Zhou
 */
public class DateUtils {

    public static String getFolderName(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String folderName = dateFormat.format(date);
        return folderName;
    }
}
