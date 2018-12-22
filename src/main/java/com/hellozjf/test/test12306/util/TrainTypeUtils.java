package com.hellozjf.test.test12306.util;

import com.hellozjf.test.test12306.constant.TrainTypeEnum;

/**
 * @author hellozjf
 */
public class TrainTypeUtils {

    /**
     * 判断该列车类型是否是我们需要的列车类型
     * @param trainCode
     * @param trainTypeEnum
     * @return
     */
    public static boolean isWantedTrainType(String trainCode, TrainTypeEnum trainTypeEnum) {
        if (trainCode.contains(trainTypeEnum.getCode())) {
            return true;
        } else {
            return false;
        }
    }
}
