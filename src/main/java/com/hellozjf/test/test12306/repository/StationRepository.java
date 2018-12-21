package com.hellozjf.test.test12306.repository;

import com.hellozjf.test.test12306.dataobject.Station;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jingfeng Zhou
 */
public interface StationRepository extends JpaRepository<Station, String> {
    Station findByCode2(String code2);
    Station findByName(String name);
}
