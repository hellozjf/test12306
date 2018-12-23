package com.hellozjf.test.test12306.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author hellozjf
 */
@Data
public class OrcResultVO {

    @JsonProperty("log_id")
    private Long logId;

    @JsonProperty("words_result")
    private List<WordResultVO> wordsResult;

    @JsonProperty("words_result_num")
    private Integer wordsResultNum;
}
