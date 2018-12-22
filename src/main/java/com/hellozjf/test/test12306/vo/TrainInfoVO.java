package com.hellozjf.test.test12306.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jingfeng Zhou
 *
 * 车票信息
 */
@Data
@NoArgsConstructor
@Slf4j
public class TrainInfoVO {

    /**
     * 从文本中获取TickerInfoVO
     * @param text 例如UN7nQhb7loK8dCU%2F2Sgax12s%2BwBol6u9c8fwKhyOdPKB5LOoOEhL0gzxN6ByC4enFf4HmdPU47uD%0AE%2BlEsP5oznjN0Udbot7GxD%2FSY2ekagY%2FIcpZrmrLTvGlZgW1fJ%2BBTQGhAuciHNWi9gWLcAcC6Ebk%0AoEzjj25DbcZS8K%2FPCEimHHCjvVIkS27UpJFFpj1vdMH8PEz5IyPFRIhE1iYpCCuhwR0atakcAc1Q%0AcQxHSDf8BUDfQeg02AEl%2F3rMhiaz						|预订|56000D316511|D3165|HGH|LYS|HGH|NGH|12:09|13:13|01:04|Y|BlyDGHiiuASAZXSrSI%2FXvvre5DCa4wtaBNRZZKtX9GGka4o1					|20181229|3|H1|01|04|1|0|||||||有||||3 |2 |  ||O0M0O0	|OMO |0
     * @return
     */
    public static TrainInfoVO getFromText(String text) {
        TrainInfoVO ticketInfoVO = new TrainInfoVO();
        String[] texts = text.split("\\|");
//        for (String t : texts) {
//            log.debug("t = {}", t);
//        }

        ticketInfoVO.code1 = texts[0];
        ticketInfoVO.orderString = texts[1];
        ticketInfoVO.ticketCode = texts[2];
        ticketInfoVO.trainCode = texts[3];
        ticketInfoVO.firstStation = texts[4];
        ticketInfoVO.lastStation= texts[5];
        ticketInfoVO.depStation= texts[6];
        ticketInfoVO.arrStation= texts[7];
        ticketInfoVO.depTime= texts[8];
        ticketInfoVO.arrTime= texts[9];
        ticketInfoVO.duringTime= texts[10];
        ticketInfoVO.code2= texts[11];
        ticketInfoVO.code3= texts[12];
        ticketInfoVO.firstStationDeptDate = texts[13];
        ticketInfoVO.code4= texts[14];
        ticketInfoVO.code5= texts[15];
        ticketInfoVO.code6= texts[16];
        ticketInfoVO.code7= texts[17];
        ticketInfoVO.code8= texts[18];
        ticketInfoVO.code9= texts[19];
        ticketInfoVO.code10= texts[20];
        ticketInfoVO.code11= texts[21];
        ticketInfoVO.code12= texts[22];
        ticketInfoVO.softBerth = texts[23];
        ticketInfoVO.code14= texts[24];
        ticketInfoVO.code15= texts[25];
        ticketInfoVO.noSeat= texts[26];
        ticketInfoVO.code17= texts[27];
        ticketInfoVO.hardBerth = texts[28];
        ticketInfoVO.hardSeat = texts[29];
        ticketInfoVO.secondClassSeat= texts[30];
        ticketInfoVO.firstClassSeat= texts[31];
        ticketInfoVO.businessSeat= texts[32];
        ticketInfoVO.code20= texts[33];
        ticketInfoVO.code21= texts[34];
        ticketInfoVO.code22= texts[35];
        ticketInfoVO.code23= texts[36];
        return ticketInfoVO;
    }

    /**
     * 未知的code
     * 有些类似于UN7nQhb7loK8dCU%2F2Sgax12s%2BwBol6u9c8fwKhyOdPKB5LOoOEhL0gzxN6ByC4enFf4HmdPU47uD%0AE%2BlEsP5oznjN0Udbot7GxD%2FSY2ekagY%2FIcpZrmrLTvGlZgW1fJ%2BBTQGhAuciHNWi9gWLcAcC6Ebk%0AoEzjj25DbcZS8K%2FPCEimHHCjvVIkS27UpJFFpj1vdMH8PEz5IyPFRIhE1iYpCCuhwR0atakcAc1Q%0AcQxHSDf8BUDfQeg02AEl%2F3rMhiaz
     * 有些不存在
     */
    private String code1;

    /**
     * 预定的字符串
     */
    private String orderString;

    /**
     * 票的code，例如56000D316511
     */
    private String ticketCode;

    /**
     * 火车的code，例如D3165
     */
    private String trainCode;

    /**
     * 首发站的code，例如HGH，就是杭州东
     */
    private String firstStation;

    /**
     * 最后站的code，例如LYS，就是龙岩
     */
    private String lastStation;

    /**
     * 出发站的code，例如HGH，就是杭州东
     */
    private String depStation;

    /**
     * 到达站的code，例如NGH，就是宁波
     */
    private String arrStation;

    /**
     * 出发时间，例如12:09
     */
    private String depTime;

    /**
     * 达到时间，例如13:13
     */
    private String arrTime;

    /**
     * 持续时间，例如01:04
     */
    private String duringTime;

    /**
     * 未知的code，例如Y
     */
    private String code2;

    /**
     * 未知的code，例如BlyDGHiiuASAZXSrSI%2FXvvre5DCa4wtaBNRZZKtX9GGka4o1
     */
    private String code3;

    /**
     * 出发日期，例如20181229
     */
    private String firstStationDeptDate;

    /**
     * 未知的code，例如3
     */
    private String code4;

    /**
     * 未知的code，例如H1
     */
    private String code5;

    /**
     * 未知的code，例如H1
     */
    private String code6;

    /**
     * 未知的code，例如01
     */
    private String code7;

    /**
     * 未知的code，例如04
     */
    private String code8;

    /**
     * 未知的code，例如1
     */
    private String code9;

    /**
     * 未知的code，例如0
     */
    private String code10;

    /**
     * 未知的code，例如空字符串
     */
    private String code11;

    /**
     * 未知的code，例如空字符串
     */
    private String code12;

    /**
     * 软卧的数量，可能是数字，有，无，空字符串
     */
    private String softBerth;

    /**
     * 未知的code，例如空字符串
     */
    private String code14;

    /**
     * 未知的code，例如空字符串
     */
    private String code15;

    /**
     * 无座票的数量，可能是数字，有，无，空字符串
     */
    private String noSeat;

    /**
     * 未知的code，例如空字符串
     */
    private String code17;

    /**
     * 硬卧的数量，可能是数字，有，无，空字符串
     */
    private String hardBerth;

    /**
     * 硬座的数量，可能是数字，有，无，空字符串
     */
    private String hardSeat;

    /**
     * 二等座的数量，可能是数字，有，无，空字符串
     */
    private String secondClassSeat;

    /**
     * 一等座的数量，可能是数字，有，无，空字符串
     */
    private String firstClassSeat;

    /**
     * 商务座的数量，可能是数字，有，无，空字符串
     */
    private String businessSeat;

    /**
     * 未知的code，例如空字符串
     */
    private String code20;

    /**
     * 未知的code，例如O0M0O0
     */
    private String code21;

    /**
     * 未知的code，例如OMO
     */
    private String code22;

    /**
     * 未知的code，例如0
     */
    private String code23;
}
