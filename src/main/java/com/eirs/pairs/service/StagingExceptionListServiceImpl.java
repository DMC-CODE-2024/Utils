package com.eirs.pairs.service;

import com.eirs.pairs.repository.StagingExceptionListRepository;
import com.eirs.pairs.repository.entity.StagingExceptionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StagingExceptionListServiceImpl implements StagingExceptionListService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StagingExceptionListRepository stagingExceptionListRepository;



    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public StagingExceptionList save(StagingExceptionList stagingExceptionList) {
        stagingExceptionList = stagingExceptionListRepository.save(stagingExceptionList);
        log.info("Saved in to StagingExceptionList:{}", stagingExceptionList);
        return stagingExceptionList;
    }
}
