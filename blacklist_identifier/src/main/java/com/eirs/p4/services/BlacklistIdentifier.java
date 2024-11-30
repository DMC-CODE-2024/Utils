package com.eirs.p4.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.DBType;
import com.eirs.model.ModuleAuditTrail;
import com.eirs.p4.constant.QueriesConstants;
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
            String query1 = QueriesConstants.QUERY_1.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query2 = QueriesConstants.QUERY_2.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query3 = QueriesConstants.QUERY_3.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query4 = QueriesConstants.QUERY_4.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query5 = QueriesConstants.QUERY_5.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            queryExecutorService.execute(query1);
            queryExecutorService.execute(query2);
            queryExecutorService.execute(query3);
            queryExecutorService.execute(query4);
            queryExecutorService.execute(query5);
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

