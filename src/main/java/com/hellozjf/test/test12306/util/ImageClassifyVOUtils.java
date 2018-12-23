package com.hellozjf.test.test12306.util;

import com.hellozjf.test.test12306.vo.ImageClassifyResultVO;
import com.hellozjf.test.test12306.vo.ImageClassifyVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hellozjf
 */
public class ImageClassifyVOUtils {

    /**
     * 从ImageClassifyVO中获取keyword列表
     * @param imageClassifyVO
     * @return
     */
    public static List<String> getKeywordList(ImageClassifyVO imageClassifyVO) {
        List<String> keywordList = new ArrayList<>();
        List<ImageClassifyResultVO> imageClassifyResultVOList = imageClassifyVO.getResult();
        for (ImageClassifyResultVO imageClassifyResultVO : imageClassifyResultVOList) {
            keywordList.add(imageClassifyResultVO.getKeyword());
        }
        return keywordList;
    }
}
