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
            String queryA1 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryA2 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI_IMSI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryA3 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryA4 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryA8 = QueriesConstants.APP_ACTIVE_UNIQUE_TAC.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            String queryB1 = QueriesConstants.APP_ACTIVE_IMEI_DIFF_MSISDN_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryB2 = QueriesConstants.APP_ACTIVE_IMEI_DIFF_MSISDN_IMEI_IMSI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryB3 = QueriesConstants.APP_ACTIVE_IMEI_DIFF_MSISDN_IMEI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryB4 = QueriesConstants.APP_ACTIVE_IMEI_DIFF_MSISDN_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryB8 = QueriesConstants.APP_ACTIVE_IMEI_DIFF_MSISDN_TAC.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            String queryC1 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC2 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI_IMSI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC3 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC4 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC8 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_TAC.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            String queryD1 = QueriesConstants.APP_EDR_ACTIVE_IMEI_DIFF_MSISDN_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD2 = QueriesConstants.APP_EDR_ACTIVE_IMEI_DIFF_MSISDN_IMEI_IMSI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD3 = QueriesConstants.APP_EDR_ACTIVE_IMEI_DIFF_MSISDN_IMEI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD4 = QueriesConstants.APP_EDR_ACTIVE_IMEI_DIFF_MSISDN_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD8 = QueriesConstants.APP_EDR_ACTIVE_IMEI_DIFF_MSISDN_TAC.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            String query1 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query2 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMEI_IMSI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query3 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMEI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query4 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            queryExecutorService.execute(queryA1);
            queryExecutorService.execute(queryA2);
            queryExecutorService.execute(queryA3);
            queryExecutorService.execute(queryA4);
            queryExecutorService.execute(queryA8);

            queryExecutorService.execute(queryB1);
            queryExecutorService.execute(queryB2);
            queryExecutorService.execute(queryB3);
            queryExecutorService.execute(queryB4);
            queryExecutorService.execute(queryB8);

            queryExecutorService.execute(queryC1);
            queryExecutorService.execute(queryC2);
            queryExecutorService.execute(queryC3);
            queryExecutorService.execute(queryC4);
            queryExecutorService.execute(queryC8);

            queryExecutorService.execute(queryD1);
            queryExecutorService.execute(queryD2);
            queryExecutorService.execute(queryD3);
            queryExecutorService.execute(queryD4);
            queryExecutorService.execute(queryD8);

            queryExecutorService.execute(query1);
            queryExecutorService.execute(query2);
            queryExecutorService.execute(query3);
            queryExecutorService.execute(query4);
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

