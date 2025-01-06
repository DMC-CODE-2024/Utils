package com.eirs.updateDuplicateMsisdn.repository;

import com.eirs.updateDuplicateMsisdn.repository.entity.Duplicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuplicateRepository extends JpaRepository<Duplicate, Long> {

    List<Duplicate> findByMsisdnIsNullOrMsisdn(String msisdn);

}
