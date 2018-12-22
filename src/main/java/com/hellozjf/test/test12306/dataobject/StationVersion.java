package com.hellozjf.test.test12306.dataobject;

import lombok.Data;

import javax.persistence.Entity;

/**
 * @author hellozjf
 *
 * 本表用于存储Station的版本
 */
@Entity
@Data
public class StationVersion extends BaseEntity {
    private String stationNameUri;
}
