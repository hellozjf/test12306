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

    @Query(
            " select question from VerificationCode where disposeResult=?1 group by question "
    )
    List<String> findByDisposeResultEqualsGroupByQuestion(String disposeResult);

    List<VerificationCode> findByDisposeResultEqualsAndQuestionEquals(String disposeResult, String question);

    /**
     * 通过folderName找到对应的VerificationCode
     * @param folderName
     * @return
     */
    VerificationCode findByFolderName(String folderName);

    /**
     * 找到一个人工未确认的处理结果
     * @return
     */
    List<VerificationCode> findTop10ByDisposeResultEqualsOrderByFolderNameAsc(String disposeResult);

    /**
     * 找到时间最早的未标注的问题
     * @return
     */
    VerificationCode findTopByChooseNullOrderByFolderNameAsc();

    /**
     * 找到下一个未标注的问题
     * @return
     */
    VerificationCode findTopByChooseNullAndFolderNameGreaterThanOrderByFolderNameAsc(String folderName);

    /**
     * 找到上一个未标注的问题
     * @param folderName
     * @return
     */
    VerificationCode findTopByChooseNullAndFolderNameLessThanOrderByFolderNameDesc(String folderName);

    /**
     * 找到比某个问题时间更大的问题
     * @return
     */
    VerificationCode findTopByFolderNameGreaterThanOrderByFolderNameAsc(String folderName);

    /**
     * 找到比某个问题时间更小的问题
     * @param folderName
     * @return
     */
    VerificationCode findTopByFolderNameLessThanOrderByFolderNameDesc(String folderName);
}
