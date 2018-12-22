package com.hellozjf.test.test12306.util;

import org.springframework.util.StringUtils;

/**
 * @author hellozjf
 *
 */
public class SeatUtils {

    /**
     * 判断有没有座位
     * @param s 可能是数字，有，无，空字符串
     * @return
     */
    public static boolean haveSeat(String s) {
        if (StringUtils.isEmpty(s) || s.equals("无")) {
            return false;
        } else {
            return true;
        }
    }
}
