package com.eirs.updateDuplicateMsisdn.repository;

import com.eirs.updateDuplicateMsisdn.repository.entity.HlrDumpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HlrDumpRepository extends JpaRepository<HlrDumpEntity, Long> {

    HlrDumpEntity findByImsi(String imsi);
}
