package com.hellozjf.test.test12306.service;

import com.hellozjf.test.test12306.vo.QuestionInfoVO;
import com.hellozjf.test.test12306.vo.ResultVO;

import javax.servlet.http.HttpSession;

/**
 * @author Jingfeng Zhou
 */
public interface IQuestionAnswer {

    /**
     * 供Controller调用的getQuestion方法
     * @param httpSession
     * @return
     */
    ResultVO getQuestion(HttpSession httpSession);

    /**
     * 供Controller调用的answerQuestion方法
     * @param questionInfoVO
     * @param httpSession
     * @param bSaveToDatabase
     * @return
     */
    ResultVO answerQuestion(QuestionInfoVO questionInfoVO, boolean bSaveToDatabase, HttpSession httpSession);
}
