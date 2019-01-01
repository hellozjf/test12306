package com.hellozjf.test.test12306.repository;

import com.hellozjf.test.test12306.dataobject.HomeAddr;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hellozjf
 */
public interface HomeAddrRepository extends JpaRepository<HomeAddr, String> {
}
