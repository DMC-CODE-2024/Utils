package com.eirs.pairMgmtClean.service;

import com.eirs.config.AppConfig;
import com.eirs.constants.DBType;
import com.eirs.constants.DateFormatterConstants;
import com.eirs.model.ModuleAuditTrail;
import com.eirs.pairMgmtClean.constant.PairMgmtInitStartQueriesConstants;
import com.eirs.services.ModuleAlertService;
import com.eirs.services.ModuleAuditTrailService;
import com.eirs.services.QueryExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class PairMgmtInitStartCleanProcess {

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

    final String MODULE_NAME = "manual_pairing";
    public void runUtility() {
        Integer count = 0;
        Long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(LocalDate.now(), appConfig.getFeatureName())) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();

        String cleanQuery = appConfig.getDbType() == DBType.MYSQL ? PairMgmtInitStartQueriesConstants.MYSQL_UPDATE_MGMT_INIT_TABLE : PairMgmtInitStartQueriesConstants.MYSQL_UPDATE_MGMT_INIT_TABLE;
        try {
            String startRange = LocalDateTime.now().withSecond(0).withMinute(0).minusHours(systemConfigurationService.getMgmtInitStartCleanUpHours()).format(DateFormatterConstants.simpleDateFormat);
            count = queryExecutorService.execute(cleanQuery.replaceAll(PairMgmtInitStartQueriesConstants.PARAM_START_RANGE, startRange));
            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            logger.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), appConfig.getFeatureName());
            updateModuleAuditTrail.setStatusCode(500);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), appConfig.getFeatureName());
            logger.error("Error while running Queries ", e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(count);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }
}

