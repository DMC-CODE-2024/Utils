package com.eirs.pairs.service;

import com.eirs.pairs.stolen.StolenGreyToBlackProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StolenGreyToBlackUtility implements UtilityService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StolenGreyToBlackProcess stolenGreyToBlackProcess;

    @Override
    public void runUtility() {
        log.info("Starting Stolen Grey to Black list process");
        stolenGreyToBlackProcess.executeQueries();
    }

}
