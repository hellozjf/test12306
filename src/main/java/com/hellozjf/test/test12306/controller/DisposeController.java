package com.hellozjf.test.test12306.controller;

import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.constant.DisposeResultEnums;
import com.hellozjf.test.test12306.dataobject.VerificationCode;
import com.hellozjf.test.test12306.repository.VerificationCodeRepository;
import com.hellozjf.test.test12306.util.ResultUtils;
import com.hellozjf.test.test12306.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author hellozjf
 */
@Controller
@Slf4j
public class DisposeController {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private CustomConfig customConfig;

    /**
     * 人工处理主页
     * @return
     */
    @GetMapping("/dispose")
    public String dispose() {
        return "dispose";
    }

    /**
     * 获取网络图片的前缀
     * @return
     */
    @GetMapping("/dispose/getNetPrefix")
    @ResponseBody
    public ResultVO getNetPrefix() {
        return ResultUtils.success(customConfig.getNetPrefix());
    }

    /**
     * 下载待人工处理的图片和问题文字
     */
    @GetMapping("/dispose/download")
    @ResponseBody
    public ResultVO download() {
        List<VerificationCode> verificationCodeList = verificationCodeRepository.findTop10ByDisposeResultEqualsOrderByFolderNameAsc(DisposeResultEnums.UNDISPOSE.getCode());
        log.debug("find top10 = {}", verificationCodeList);
        return ResultUtils.success(verificationCodeList);
    }

    /**
     * 上传人工处理结果
     */
    @PostMapping("/dispose/upload")
    @ResponseBody
    public ResultVO upload(@RequestBody List<VerificationCode> verificationCodeList) {
        log.debug("verificationCodeList = {}", verificationCodeList);
        for (VerificationCode verificationCode : verificationCodeList) {
            verificationCode.setDisposeResult(DisposeResultEnums.MANUAL_DISPOSE.getCode());
        }
        verificationCodeRepository.saveAll(verificationCodeList);
        return ResultUtils.success();
    }
}
