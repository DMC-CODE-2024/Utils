package com.eirs.pairUpdateMsisdn.repository;

import com.eirs.pairUpdateMsisdn.repository.entity.HlrDumpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HlrDumpRepository extends JpaRepository<HlrDumpEntity, Long> {

    HlrDumpEntity findByImsi(String imsi);
}
