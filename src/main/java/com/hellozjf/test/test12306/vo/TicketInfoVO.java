package com.hellozjf.test.test12306.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jingfeng Zhou
 *
 * 车票信息
 */
@Data
@NoArgsConstructor
public class TicketInfoVO {

    private String ticketCode;
    private String trainCode;
    private String firstStation;
    private String lastStation;
    private String depStation;
    private String arrStation;
    private String depTime;
    private String arrTime;
}
