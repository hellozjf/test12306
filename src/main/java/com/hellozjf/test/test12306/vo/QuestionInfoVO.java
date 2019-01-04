package com.hellozjf.test.test12306.vo;

import lombok.Data;

import java.util.Map;

/**
 * @author hellozjf
 */
@Data
public class QuestionInfoVO {

    private String questionUrl;
    private String pic00Url;
    private String pic01Url;
    private String pic02Url;
    private String pic03Url;
    private String pic10Url;
    private String pic11Url;
    private String pic12Url;
    private String pic13Url;

    /**
     * 标识是哪个问题
     */
    private String folderName;

    /**
     * 问题
     */
    private String question;

    /**
     * 按逗号隔开的字符串，表示选中的图片，范围1-8
     */
    private String choose;
}
