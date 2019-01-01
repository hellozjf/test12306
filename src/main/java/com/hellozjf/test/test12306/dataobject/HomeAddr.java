package com.hellozjf.test.test12306.dataobject;

import lombok.Data;

import javax.persistence.Entity;

/**
 * @author hellozjf
 */
@Data
@Entity
public class HomeAddr extends BaseEntity {
    private String ip;
}
