package com.hellozjf.test.test12306.controller;

import com.hellozjf.test.test12306.dataobject.HomeAddr;
import com.hellozjf.test.test12306.repository.HomeAddrRepository;
import com.hellozjf.test.test12306.util.ResultUtils;
import com.hellozjf.test.test12306.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hellozjf
 *
 * 我家里有台电脑，这个controller专门用来接收家里电脑过来的IP地址
 */
@RestController
public class AcceptHomeAddrController {

    @Autowired
    private HomeAddrRepository homeAddrRepository;

    @PostMapping("/postHomeAddr")
    public ResultVO postHomeAddr(String ip) {
        long count = homeAddrRepository.count();
        if (count == 0) {
            HomeAddr homeAddr = new HomeAddr();
            homeAddr.setIp(ip);
            homeAddrRepository.save(homeAddr);
        } else {
            HomeAddr homeAddr = homeAddrRepository.findAll().get(0);
            if (! homeAddr.getIp().equals(ip)) {
                homeAddr.setIp(ip);
                homeAddrRepository.save(homeAddr);
            }
        }

        return ResultUtils.success(ip);
    }
}
