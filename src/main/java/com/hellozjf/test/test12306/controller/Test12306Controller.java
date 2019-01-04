package com.hellozjf.test.test12306.controller;

import com.hellozjf.test.test12306.service.IQuestionAnswer;
import com.hellozjf.test.test12306.vo.QuestionInfoVO;
import com.hellozjf.test.test12306.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author hellozjf
 */
@Controller
@Slf4j
public class Test12306Controller {

    @Autowired
    private IQuestionAnswer questionAnswer;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/getUncheckQuestion")
    @ResponseBody
    public ResultVO getQuestion(HttpSession httpSession) {
        return questionAnswer.getQuestion(httpSession);
    }

    @PostMapping("/answerQuestion")
    @ResponseBody
    public ResultVO answerQuestion(QuestionInfoVO questionInfoVO, HttpSession httpSession) {
        return questionAnswer.answerQuestion(questionInfoVO, httpSession);
    }
}
