package com.hellozjf.test.test12306.dataobject;

import lombok.Data;

import javax.persistence.Entity;

/**
 * @author hellozjf
 *
 * 我在阿里云上会创建一个文件夹，叫/mnt/vdb1/ftp/hellozjf/Pictures/12306
 * 在这个文件夹下面，会按照时间创建文件夹，例如20181227015230567，分别对应年月日时分秒毫秒
 * 在这个时间文件夹下面会有
 * 一张总图片，叫all.jpg
 * 一张问题图片，叫question.jpg
 * 八张子图片，分别叫pic00.jpg, pic01.jpg, ……, pic13.jpg
 */
@Entity
@Data
public class VerificationCode extends BaseEntity {

    /**
     * 文件夹的名称，例如20181227015230567
     */
    private String folderName;

    /**
     * 识别出来的问题
     */
    private String question;

    /**
     * 第i行第j列子图的描述
     */
    private String pic00Desc;
    private String pic01Desc;
    private String pic02Desc;
    private String pic03Desc;
    private String pic10Desc;
    private String pic11Desc;
    private String pic12Desc;
    private String pic13Desc;

    /**
     * 处理的结果
     * 0：未处理
     * 1：人工已处理
     * 2：机器已采纳
     */
    private String disposeResult;

    /**
     * [0][0]为1，[1][3]为8，多个按逗号隔开
     */
    private String choose;
}
