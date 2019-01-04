package com.hellozjf.test.test12306.service;

import com.hellozjf.test.test12306.vo.QuestionInfoVO;
import com.hellozjf.test.test12306.vo.ResultVO;

import javax.servlet.http.HttpSession;

/**
 * @author Jingfeng Zhou
 */
public interface IQuestionAnswer {

    ResultVO getQuestion(HttpSession httpSession);
    ResultVO answerQuestion(QuestionInfoVO questionInfoVO, HttpSession httpSession);
}
