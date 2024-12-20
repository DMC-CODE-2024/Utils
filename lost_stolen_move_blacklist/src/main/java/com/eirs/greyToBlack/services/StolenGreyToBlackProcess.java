package com.eirs.greyToBlack.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.DBType;
import com.eirs.constants.DateFormatterConstants;
import com.eirs.constants.pairing.DeviceSyncOperation;
import com.eirs.model.ModuleAuditTrail;
import com.eirs.services.ModuleAlertService;
import com.eirs.services.ModuleAuditTrailService;
import com.eirs.services.QueryExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class StolenGreyToBlackProcess {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QueryExecutorService queryExecutorService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private SystemConfigurationService systemConfigurationService;
    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;
    @Autowired
    private ModuleAlertService moduleAlertService;

    final String MODULE_NAME = "lost_stolen";

    @Transactional
    public void executeQueries() {
        Long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(localDate, appConfig.getFeatureName())) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();

        String INSERT_INTO_GREY_HIS_TABLE = appConfig.getDbType() == DBType.MYSQL ? QueriesConstants.INSERT_INTO_GREY_HIS_TABLE : QueriesConstants.ORACLE_INSERT_INTO_GREY_HIS_TABLE;
        String INSERT_INTO_BLACK_LIST_TABLE = appConfig.getDbType() == DBType.MYSQL ? QueriesConstants.INSERT_INTO_BLACK_LIST_TABLE : QueriesConstants.ORACLE_INSERT_INTO_BLACK_LIST_TABLE;
        String INSERT_INTO_BLACK_LIST_HIS_TABLE = appConfig.getDbType() == DBType.MYSQL ? QueriesConstants.INSERT_INTO_BLACK_LIST_HIS_TABLE : QueriesConstants.ORACLE_INSERT_INTO_BLACK_LIST_HIS_TABLE;
        String nowDate = appConfig.getDbType() == DBType.MYSQL ? "NOW()" : "sysdate";
        try {
            String dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0, 0)).format(DateFormatterConstants.simpleDateFormat);
            queryExecutorService.execute(INSERT_INTO_GREY_HIS_TABLE.replaceAll(QueriesConstants.CURRENT_TIME, nowDate).replaceAll(QueriesConstants.PARAM_DATE_TIME, dateTime).replaceAll(QueriesConstants.PARAM_OPERATION, String.valueOf(DeviceSyncOperation.DELETE.ordinal())));
            queryExecutorService.execute(INSERT_INTO_BLACK_LIST_TABLE.replaceAll(QueriesConstants.CURRENT_TIME, nowDate).replaceAll(QueriesConstants.PARAM_DATE_TIME, dateTime));
            queryExecutorService.execute(INSERT_INTO_BLACK_LIST_HIS_TABLE.replaceAll(QueriesConstants.CURRENT_TIME, nowDate).replaceAll(QueriesConstants.PARAM_DATE_TIME, dateTime).replaceAll(QueriesConstants.PARAM_OPERATION, String.valueOf(DeviceSyncOperation.ADD.ordinal())));
            Integer deletedCount = queryExecutorService.execute(QueriesConstants.DELETE_FROM_GREY_LIST_TABLE.replaceAll(QueriesConstants.PARAM_DATE_TIME, dateTime));
            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            logger.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), appConfig.getFeatureName());
            updateModuleAuditTrail.setStatusCode(500);
        } catch (Exception e) {
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), appConfig.getFeatureName());
            logger.error("Error while running Queries ", e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(0);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }
}
