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
    FOLDER_NAME_IS_EMPTY(4, "图片目录为空"),
    ANSWER_ERROR(5, "回答问题错误"),
    LOGIN_ERROR(6, "登录失败"),
    CAN_NOT_GET_IMAGE(7, "无法获取问题图片"),
    UNKNOWN_ERROR(8, "未知错误"),
    REFRESH_TOO_FAST(9, "刷新的频率太快"),
    WRITE_FILE_ERROR(10, "写入文件出错"),
    CREATE_FOLDER_ERROR(11, "创建文件夹失败"),
    ;

    Integer code;
    String message;
}
