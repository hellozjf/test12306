package com.hellozjf.test.test12306.dataobject;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;

/**
 * @author Jingfeng Zhou
 *
 * 这张表主要用于存放12306各个火车站点的名称和编码等信息，它的版本位于StationVersion表
 */
@Entity
@Data
@Slf4j
@NoArgsConstructor
public class Station extends BaseEntity {

    public static Station getFromText(String text) {
        Station station = new Station();
        String[] texts = text.split("\\|");
//        for (String t : texts) {
//            log.debug("t = {}", t);
//        }
        station.code1 = texts[0];
        station.name = texts[1];
        station.code2 = texts[2];
        station.spellFull = texts[3];
        station.spellShort = texts[4];
        station.number = Integer.valueOf(texts[5]);
        return station;
    }

    /**
     * 例如杭州东是44
     */
    private Integer number;
    /**
     * 例如杭州东是杭州东
     */
    private String name;
    /**
     * 例如杭州东是hangzhoudong
     */
    private String spellFull;
    /**
     * 例如杭州东是hzd
     */
    private String spellShort;
    /**
     * 例如杭州东是hzd
     */
    private String code1;
    /**
     * 例如杭州东是HGH
     */
    private String code2;
}
