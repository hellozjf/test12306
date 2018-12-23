package com.hellozjf.test.test12306.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hellozjf
 */
@Data
public class ImageClassifyVO {

    @JsonProperty("log_id")
    private Long logId;

    @JsonProperty("result_num")
    private Integer resultNum;

    @JsonProperty("result")
    private List<ImageClassifyResultVO> result;
}
