package com.hellozjf.test.test12306.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hellozjf
 */
@Getter
@AllArgsConstructor
public enum TrainTypeEnum {

    G("G", "高铁"),
    D("D", "动车"),
    K("K", "特快"),
    ;

    String code;
    String desc;
}
