package com.eirs.duplicateExpiry.repository;

import com.eirs.duplicateExpiry.repository.entity.ImeiPairDetailHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImeiPairDetailHisRepository extends JpaRepository<ImeiPairDetailHis, Long> {

}
