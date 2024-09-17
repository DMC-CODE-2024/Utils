package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.StagingExceptionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StagingExceptionListRepository extends JpaRepository<StagingExceptionList, Long> {

}
