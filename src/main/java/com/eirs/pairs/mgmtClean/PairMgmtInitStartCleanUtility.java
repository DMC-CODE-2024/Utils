package com.eirs.pairs.mgmtClean;

import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.constants.DBType;
import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.repository.entity.ModuleAuditTrail;
import com.eirs.pairs.service.*;
import com.eirs.pairs.utils.DateFormatterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PairMgmtInitStartCleanUtility implements UtilityService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QueryExecutorService queryExecutorService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    SystemConfigurationService systemConfigurationService;

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    @Autowired
    ModuleAlertService moduleAlertService;

    @Override
    @Transactional
    public void runUtility() {
        String MODULE_NAME = appConfig.getModuleName(UtilityType.PAIR_MGMT_INIT_START_CLEAN);
        Integer count = 0;
        Long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(LocalDate.now(), MODULE_NAME)) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", MODULE_NAME, localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(MODULE_NAME).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(MODULE_NAME).build();

        String cleanQuery = appConfig.getDbType() == DBType.MYSQL ? PairMgmtInitStartQueriesConstants.MYSQL_UPDATE_MGMT_INIT_TABLE : PairMgmtInitStartQueriesConstants.MYSQL_UPDATE_MGMT_INIT_TABLE;
        try {
            String startRange = LocalDateTime.now().withSecond(0).withMinute(0).minusHours(systemConfigurationService.getMgmtInitStartCleanUpHours()).format(DateFormatterConstants.simpleDateFormat);
            count = queryExecutorService.execute(cleanQuery.replaceAll(PairMgmtInitStartQueriesConstants.PARAM_START_RANGE, startRange));
            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            logger.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), MODULE_NAME);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), MODULE_NAME);
            logger.error("Error while running Queries ", e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(count);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }
}

