package com.eirs.audit.services;

import com.eirs.audit.repository.EirlistOutputAuditRepository;
import com.eirs.audit.repository.entity.EirlistOutputAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EirlistOutputAuditService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EirlistOutputAuditRepository repository;


    public void save(List<EirlistOutputAudit> entities) {
        logger.info("Saving EirlistOutputAudit List Size:{}", entities.size());
        repository.saveAll(entities);
        logger.info("Saved EirlistOutputAudit List Size:{}", entities.size());
    }
}
