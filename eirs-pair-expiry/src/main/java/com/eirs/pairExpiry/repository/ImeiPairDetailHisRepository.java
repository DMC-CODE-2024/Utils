package com.eirs.pairExpiry.repository;

import com.eirs.pairExpiry.repository.entity.ImeiPairDetailHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImeiPairDetailHisRepository extends JpaRepository<ImeiPairDetailHis, Long> {

}
