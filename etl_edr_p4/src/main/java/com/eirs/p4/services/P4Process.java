package com.eirs.p4.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.DBType;
import com.eirs.model.ModuleAuditTrail;
import com.eirs.p4.config.P4AppConfig;
import com.eirs.p4.constant.P4QueriesConstants;
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
public class P4Process {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QueryExecutorService queryExecutorService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private P4AppConfig p4AppConfig;
    DateTimeFormatter edrTableFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    SystemConfigurationService systemConfigurationService;
    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    @Autowired
    private ModuleAlertService moduleAlertService;

    final String MODULE_NAME = "etl_edr";

    public void executeQueries(LocalDate localDate) {
        if (appConfig.getDbType() == DBType.MYSQL)
            processAsMySQL(localDate);
        else processAsOracle(localDate);
    }

    private void dropEdrTable() {
        String date = LocalDate.now().minusDays(systemConfigurationService.getEdrTableCleanDays()).format(edrTableFormat);
        String dropTable = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.DROP_EDR_TABLE : P4QueriesConstants.DROP_EDR_TABLE_ORACLE;
        try {
            queryExecutorService.executeCreate(dropTable.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, date));
        } catch (Exception e) {
            logger.error("Error while dropping table:{} Error:{}", dropTable, e.getMessage());
        }
    }

    private void createTableNextDays() {
        String createEdrTable = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_EDR_TABLE_MYSQL : P4QueriesConstants.CREATE_EDR_TABLE_ORACLE;
        queryExecutorService.executeCreate(createEdrTable.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, LocalDate.now().plusDays(1).format(edrTableFormat)));
        queryExecutorService.executeCreate(createEdrTable.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, LocalDate.now().plusDays(2).format(edrTableFormat)));
        queryExecutorService.executeCreate(createEdrTable.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, LocalDate.now().plusDays(3).format(edrTableFormat)));

    }

    private void createIndexes(LocalDate edrTableDate) {
        String index1 = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_INDEX_TAC_MYSQL : P4QueriesConstants.CREATE_INDEX_TAC_ORACLE;
        String index2 = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_INDEX_MSISDN_MYSQL : P4QueriesConstants.CREATE_INDEX_MSISDN_ORACLE;
        String index3 = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_INDEX_IMEI_MYSQL : P4QueriesConstants.CREATE_INDEX_IMEI_ORACLE;
        String index4 = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_INDEX_ACTUAL_IMEI_MYSQL : P4QueriesConstants.CREATE_INDEX_ACTUAL_IMEI_ORACLE;
        String index5 = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_INDEX_DEVICE_ACTUAL_IMEI_MYSQL : P4QueriesConstants.CREATE_INDEX_DEVICE_ACTUAL_IMEI_ORACLE;
        String index6 = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_INDEX_IMEI_LENGTH_MYSQL : P4QueriesConstants.CREATE_INDEX_IMEI_LENGTH_ORACLE;
        String index7 = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_INDEX_IS_GSMA_VALID_MYSQL : P4QueriesConstants.CREATE_INDEX_IS_GSMA_VALID_ORACLE;
        String index8 = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_INDEX_IS_DUPLICATE_MYSQL : P4QueriesConstants.CREATE_INDEX_IS_DUPLICATE_ORACLE;
        String index9 = appConfig.getDbType() == DBType.MYSQL ? P4QueriesConstants.CREATE_INDEX_IS_PAIRED_MYSQL : P4QueriesConstants.CREATE_INDEX_IS_PAIRED_ORACLE;

        queryExecutorService.executeCreate(index1.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate.format(edrTableFormat)));

        queryExecutorService.executeCreate(index2.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate.format(edrTableFormat)));

        queryExecutorService.executeCreate(index3.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate.format(edrTableFormat)));

        queryExecutorService.executeCreate(index4.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate.format(edrTableFormat)));

        queryExecutorService.executeCreate(index5.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate.format(edrTableFormat)));

        queryExecutorService.executeCreate(index6.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate.format(edrTableFormat)));

        queryExecutorService.executeCreate(index7.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate.format(edrTableFormat)));

        queryExecutorService.executeCreate(index8.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate.format(edrTableFormat)));

        queryExecutorService.executeCreate(index9.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate.format(edrTableFormat)));

    }

    private void processAsMySQL(LocalDate localDate) {
        String edrTableDate = localDate.format(edrTableFormat);
        Long start = System.currentTimeMillis();
        if (!moduleAuditTrailService.previousDependentModuleExecuted(localDate, appConfig.getDependentFeatureName())) {
            logger.info("Process:{} will not execute as already Dependent Module:{} Not Executed for the day {}", appConfig.getFeatureName(), appConfig.getDependentFeatureName(), localDate);
            return;
        }
        if (!moduleAuditTrailService.canProcessRun(localDate, appConfig.getFeatureName())) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        try {
            createIndexes(localDate);
            String getMaxNoOfRows = P4QueriesConstants.SELECT_MAX_ID_NO.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate);
            Long maxId = queryExecutorService.getJdbcTemplate().queryForObject(getMaxNoOfRows, Long.class);
            logger.info("MaxId:{} in EDR table Query:{}", maxId, getMaxNoOfRows);
            Long batchSize = p4AppConfig.getBatchSize();
            long noOfBatches = maxId / batchSize;
            long remainder = maxId % batchSize;
            if (remainder > 0)
                noOfBatches++;
            logger.info("P4 Process started for localDate:{} maxId:{}, batchSize:{}, noOfBatches:{}", localDate, maxId, batchSize, noOfBatches);

            long startId = 0;
            long endId = batchSize;
            for (int batch = 1; batch <= noOfBatches; ) {
                logger.info("Processing for BatchNo:{} noOfBatches:{} startId:{} endId:{}", batch, noOfBatches, startId, endId);
                String gsmaAndDeviceType = P4QueriesConstants.UPDATE_GSMA_AND_DEVICE_TYPE.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                String gsmaLengthInvalid = P4QueriesConstants.UPDATE_GSMA_LENGTH_INVALID.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                String gsmaNonNumericInvalid = P4QueriesConstants.UPDATE_GSMA_NON_NUMERIC_INVALID.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                String updateCustomFlag = P4QueriesConstants.UPDATE_CUSTOM_FLAG.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                String updateMsisdn = P4QueriesConstants.UPDATE_MSISDN.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                String updateOperator = P4QueriesConstants.UPDATE_OPERATOR.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                String updateIsDuplicate = P4QueriesConstants.UPDATE_DUPLICATE_IMEI_RECORDS_MYSQL.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                String updateIsDuplicateDevice = P4QueriesConstants.UPDATE_DUPLICATE_DEVICE_RECORDS_MYSQL.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                String gsmaInvalidWithInvalidImei = P4QueriesConstants.UPDATE_IS_GSMA_WITH_INVALID_IMEI_MYSQL.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                String updateIsPairedDevice = P4QueriesConstants.UPDATE_IS_PAIRED_RECORDS_MYSQL.replaceAll(P4QueriesConstants.START_ID, String.valueOf(startId)).replaceAll(P4QueriesConstants.END_ID, String.valueOf(endId));
                queryExecutorService.execute(gsmaAndDeviceType.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
                queryExecutorService.execute(gsmaLengthInvalid.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
                queryExecutorService.execute(gsmaNonNumericInvalid.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
                queryExecutorService.execute(updateCustomFlag.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
                queryExecutorService.execute(updateMsisdn.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
                queryExecutorService.execute(updateOperator.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
                queryExecutorService.execute(updateIsDuplicate.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
                queryExecutorService.execute(updateIsDuplicateDevice.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
                queryExecutorService.execute(gsmaInvalidWithInvalidImei.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
                queryExecutorService.execute(updateIsPairedDevice.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));

                startId = endId;
                batch++;
                endId = (batch * batchSize);
            }
            updateModuleAuditTrail.setStatusCode(200);
//            createTableNextDays();
//            createIndexes();
            dropEdrTable();

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

    private void processAsOracle(LocalDate localDate) {
        String edrTableDate = localDate.format(edrTableFormat);
        Long start = System.currentTimeMillis();
        LocalDate now = LocalDate.now();
        if (!moduleAuditTrailService.previousDependentModuleExecuted(localDate, appConfig.getDependentFeatureName())) {
            logger.info("Process:{} will not execute as already Dependent Module:{} Not Executed for the day {}", appConfig.getFeatureName(), appConfig.getDependentFeatureName(), localDate);
            return;
        }
        if (!moduleAuditTrailService.canProcessRun(now, appConfig.getFeatureName())) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), now);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        try {
            String gsmaAndDeviceType1 = P4QueriesConstants.UPDATE_GSMA_AND_DEVICE_TYPE_ORACLE_1;
            String gsmaAndDeviceType2 = P4QueriesConstants.UPDATE_GSMA_AND_DEVICE_TYPE_ORACLE_2;
            String gsmaLengthInvalid = P4QueriesConstants.UPDATE_GSMA_LENGTH_INVALID_ORACLE;
            String gsmaNonNumericInvalid = P4QueriesConstants.UPDATE_GSMA_NON_NUMERIC_INVALID_ORACLE;
            String updateCustomFlag = P4QueriesConstants.UPDATE_CUSTOM_FLAG_ORACLE;
            String updateMsisdn1 = P4QueriesConstants.UPDATE_MSISDN_ORACLE;
            String updateOperator = P4QueriesConstants.UPDATE_OPERATOR_ORACLE;
            queryExecutorService.execute(gsmaAndDeviceType1.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
            queryExecutorService.execute(gsmaAndDeviceType2.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
            queryExecutorService.execute(gsmaLengthInvalid.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
            queryExecutorService.execute(gsmaNonNumericInvalid.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
            queryExecutorService.execute(updateCustomFlag.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
            queryExecutorService.execute(updateMsisdn1.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));
            queryExecutorService.execute(updateOperator.replaceAll(P4QueriesConstants.PARAM_YYYYMMDD, edrTableDate));

            updateModuleAuditTrail.setStatusCode(200);
//            createTableNextDays();
//            createIndexes();
            dropEdrTable();
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

