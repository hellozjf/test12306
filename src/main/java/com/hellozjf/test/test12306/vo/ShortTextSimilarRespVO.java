package com.hellozjf.test.test12306.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author hellozjf
 */
@Data
public class ShortTextSimilarRespVO {

    @JsonProperty("log_id")
    private Long logId;

    @JsonProperty("texts")
    private ShortTextSimilarReqVO texts;

    @JsonProperty("score")
    private Double score;
}
