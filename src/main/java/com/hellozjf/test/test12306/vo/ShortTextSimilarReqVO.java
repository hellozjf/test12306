package com.hellozjf.test.test12306.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author hellozjf
 */
@Data
public class ShortTextSimilarReqVO {

    @JsonProperty("text_1")
    private String text1;

    @JsonProperty("text_2")
    private String text2;
}
