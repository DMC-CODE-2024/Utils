package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.HlrDumpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HlrDumpRepository extends JpaRepository<HlrDumpEntity, Long> {

    HlrDumpEntity findByImsi(String imsi);
}
