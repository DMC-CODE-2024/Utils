package com.eirs.audit.repository;

import com.eirs.audit.repository.entity.EirlistOutputAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EirlistOutputAuditRepository extends JpaRepository<EirlistOutputAudit, Long> {

}
