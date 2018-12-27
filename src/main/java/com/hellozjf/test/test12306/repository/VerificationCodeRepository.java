package com.hellozjf.test.test12306.repository;

import com.hellozjf.test.test12306.dataobject.VerificationCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author hellozjf
 */
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {

    VerificationCode findTopByChooseNullOrderByFolderNameAsc();
}
