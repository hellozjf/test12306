package com.hellozjf.test.test12306.repository;

import com.hellozjf.test.test12306.dataobject.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hellozjf
 */
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {
}
