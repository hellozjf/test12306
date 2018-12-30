package com.hellozjf.test.test12306.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hellozjf
 */
@Getter
@AllArgsConstructor
public enum DisposeResultEnums {

    UNDISPOSE("0", "未处理"),
    MANUAL_DISPOSE("1", "人工已处理"),
    MACHINE_DISPOSE("2", "机器已采纳"),
    ;

    String code;
    String desc;
}
