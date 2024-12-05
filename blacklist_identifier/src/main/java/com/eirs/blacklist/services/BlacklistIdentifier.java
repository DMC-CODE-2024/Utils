package com.eirs.blacklist.services;

import com.eirs.blacklist.constant.QueriesConstants;
import com.eirs.config.AppConfig;
import com.eirs.model.ModuleAuditTrail;
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
import java.time.format.DateTimeFormatter;

@Service
public class BlacklistIdentifier {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QueryExecutorService queryExecutorService;

    @Autowired
    private AppConfig appConfig;
    DateTimeFormatter edrTableFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    @Autowired
    private ModuleAlertService moduleAlertService;

    final String MODULE_NAME = "eir_validator";

    public void executeQueries(LocalDate localDate) {
        String startDate = localDate.format(edrTableFormat);
        String endDate = localDate.plusDays(1).format(edrTableFormat);
        Long start = System.currentTimeMillis();
        if (!moduleAuditTrailService.canProcessRun(localDate, appConfig.getFeatureName())) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        try {
            String query1 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query2 = QueriesConstants.APP_ACTIVE_UNIQUE_TAC.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query3 = QueriesConstants.APP_ACTIVE_IMEI_DIFF_MSISDN_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query4 = QueriesConstants.APP_ACTIVE_IMEI_DIFF_MSISDN_TAC.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query5 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query6 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_TAC.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query7 = QueriesConstants.APP_EDR_ACTIVE_IMEI_DIFF_MSISDN_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query8 = QueriesConstants.APP_EDR_ACTIVE_IMEI_DIFF_MSISDN_TAC.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query9 = QueriesConstants.DELETE_WHITE_LIST_RECORDS.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            queryExecutorService.execute(query1);
            queryExecutorService.execute(query2);
            queryExecutorService.execute(query3);
            queryExecutorService.execute(query4);
            queryExecutorService.execute(query5);
            queryExecutorService.execute(query6);
            queryExecutorService.execute(query7);
            queryExecutorService.execute(query8);
            queryExecutorService.execute(query9);
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
        updateModuleAuditTrail.setCount(0);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }


}

