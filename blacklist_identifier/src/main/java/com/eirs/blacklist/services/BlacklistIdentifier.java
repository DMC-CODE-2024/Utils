package com.eirs.blacklist.services;

import com.eirs.blacklist.config.BlacklistIdentifierConfig;
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

    @Autowired
    private BlacklistIdentifierConfig blacklistIdentifierConfig;
    DateTimeFormatter edrTableFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    @Autowired
    private ModuleAlertService moduleAlertService;

    final String MODULE_NAME = "blacklist identify";

    public void executeQueries(LocalDate localDate) {
        String startDate = localDate.format(edrTableFormat);
        String endDate = localDate.plusDays(1).format(edrTableFormat);
        Integer count = 0;
        Long start = System.currentTimeMillis();
        if (!moduleAuditTrailService.canProcessRun(localDate, appConfig.getFeatureName())) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        try {
            String queryA1 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryA2 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI_IMSI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryA3 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryA4 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryA5 = QueriesConstants.APP_ACTIVE_UNIQUE_IMSI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate).replaceAll(QueriesConstants.IMEI_PATTERN, blacklistIdentifierConfig.getImeiNullPattern());
            String queryA6 = QueriesConstants.APP_ACTIVE_UNIQUE_IMSI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate).replaceAll(QueriesConstants.IMEI_PATTERN, blacklistIdentifierConfig.getImeiNullPattern());
            String queryA7 = QueriesConstants.APP_ACTIVE_UNIQUE_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate).replaceAll(QueriesConstants.IMEI_PATTERN, blacklistIdentifierConfig.getImeiNullPattern());
            String queryA8 = QueriesConstants.APP_ACTIVE_UNIQUE_TAC.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            String queryB1 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_MSISDN).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryB2 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI_IMSI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_MSISDN).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryB3 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_MSISDN).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryB4 = QueriesConstants.APP_ACTIVE_UNIQUE_IMEI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_MSISDN).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryB5 = QueriesConstants.APP_ACTIVE_UNIQUE_IMSI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_MSISDN).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate).replaceAll(QueriesConstants.IMEI_PATTERN, blacklistIdentifierConfig.getImeiNullPattern());
            String queryB6 = QueriesConstants.APP_ACTIVE_UNIQUE_IMSI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_MSISDN).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate).replaceAll(QueriesConstants.IMEI_PATTERN, blacklistIdentifierConfig.getImeiNullPattern());
            String queryB7 = QueriesConstants.APP_ACTIVE_UNIQUE_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_MSISDN).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate).replaceAll(QueriesConstants.IMEI_PATTERN, blacklistIdentifierConfig.getImeiNullPattern());
            String queryB8 = QueriesConstants.APP_ACTIVE_UNIQUE_TAC.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.CDR_TABLE_UNIQUE_MSISDN).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            String queryC1 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC2 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI_IMSI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC3 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC4 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC5 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMSI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC6 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMSI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC7 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryC8 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_TAC.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMEI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            String queryD1 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMSI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD2 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI_IMSI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMSI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD3 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMSI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD4 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMEI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMSI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD5 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMSI_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMSI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD6 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_IMSI.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMSI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD7 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_MSISDN.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMSI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String queryD8 = QueriesConstants.APP_EDR_ACTIVE_UNIQUE_TAC.replaceAll(QueriesConstants.TABLE_NAME, QueriesConstants.EDR_TABLE_UNIQUE_IMSI).replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            String query1 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMEI_IMSI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query2 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMEI_IMSI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query3 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMEI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query4 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMEI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query5 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMSI_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query6 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_IMSI.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);
            String query7 = QueriesConstants.DELETE_WHITE_LIST_RECORDS_MSISDN.replaceAll(QueriesConstants.PARAM_START_RANGE, startDate).replaceAll(QueriesConstants.PARAM_END_RANGE, endDate);

            count = queryExecutorService.execute(queryA1);
            count = count + queryExecutorService.execute(queryA2);
            count = count + queryExecutorService.execute(queryA3);
            count = count + queryExecutorService.execute(queryA4);
            count = count + queryExecutorService.execute(queryA5);
            count = count + queryExecutorService.execute(queryA6);
            count = count + queryExecutorService.execute(queryA7);
            count = count + queryExecutorService.execute(queryA8);

            count = count + queryExecutorService.execute(queryB1);
            count = count + queryExecutorService.execute(queryB2);
            count = count + queryExecutorService.execute(queryB3);
            count = count + queryExecutorService.execute(queryB4);
            count = count + queryExecutorService.execute(queryB5);
            count = count + queryExecutorService.execute(queryB6);
            count = count + queryExecutorService.execute(queryB7);
            count = count + queryExecutorService.execute(queryB8);

            count = count + queryExecutorService.execute(queryC1);
            count = count + queryExecutorService.execute(queryC2);
            count = count + queryExecutorService.execute(queryC3);
            count = count + queryExecutorService.execute(queryC4);
            count = count + queryExecutorService.execute(queryC5);
            count = count + queryExecutorService.execute(queryC6);
            count = count + queryExecutorService.execute(queryC7);
            count = count + queryExecutorService.execute(queryC8);

            count = count + queryExecutorService.execute(queryD1);
            count = count + queryExecutorService.execute(queryD2);
            count = count + queryExecutorService.execute(queryD3);
            count = count + queryExecutorService.execute(queryD4);
            count = count + queryExecutorService.execute(queryD5);
            count = count + queryExecutorService.execute(queryD6);
            count = count + queryExecutorService.execute(queryD7);
            count = count + queryExecutorService.execute(queryD8);

            count = count - queryExecutorService.execute(query1);
            count = count - queryExecutorService.execute(query2);
            count = count - queryExecutorService.execute(query3);
            count = count - queryExecutorService.execute(query4);
            count = count - queryExecutorService.execute(query5);
            count = count - queryExecutorService.execute(query6);
            count = count - queryExecutorService.execute(query7);
            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            logger.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), appConfig.getFeatureName());
            updateModuleAuditTrail.setStatusCode(501);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), appConfig.getFeatureName());
            logger.error("Error while running Queries ", e);
            updateModuleAuditTrail.setStatusCode(501);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(count);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }


}

