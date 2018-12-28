package com.hellozjf.test.test12306.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jingfeng Zhou
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    CAN_NOT_GET_UNANSWERED_QUESTION(1, "无法找到未回答的问题"),
    QUESTION_IS_EMPTY(2, "问题为空"),
    ANSWER_IS_EMPTY(3, "答案为空"),
    FOLDER_NAME_IS_EMPTY(4, "图片目录为空")
    ;

    Integer code;
    String message;
}
