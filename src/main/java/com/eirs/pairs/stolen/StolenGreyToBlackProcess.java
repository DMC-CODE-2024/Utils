package com.eirs.pairs.stolen;

import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.constants.DBType;
import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.constants.pairing.DeviceSyncOperation;
import com.eirs.pairs.repository.entity.ModuleAuditTrail;
import com.eirs.pairs.service.ModuleAlertService;
import com.eirs.pairs.service.ModuleAuditTrailService;
import com.eirs.pairs.service.QueryExecutorService;
import com.eirs.pairs.service.SystemConfigurationService;
import com.eirs.pairs.utils.DateFormatterConstants;
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
    private String moduleName = UtilityType.GREY_TO_BLACKLIST.name();
    @Autowired
    private ModuleAlertService moduleAlertService;

    @Transactional
    public void executeQueries() {
        Long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(localDate, moduleName)) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", moduleName, localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().moduleName(moduleName).featureName(moduleName).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(moduleName).featureName(moduleName).build();

        String SELECT_GREY_LIST_EXIST_IN_STOLEN_TABLE = appConfig.getDbType() == DBType.MYSQL ? QueriesConstants.MYSQL_SELECT_GREY_LIST_EXIST_IN_STOLEN_TABLE : QueriesConstants.ORACLE_SELECT_GREY_LIST_EXIST_IN_STOLEN_TABLE;
        String INSERT_INTO_GREY_HIS_TABLE = appConfig.getDbType() == DBType.MYSQL ? QueriesConstants.INSERT_INTO_GREY_HIS_TABLE : QueriesConstants.ORACLE_INSERT_INTO_GREY_HIS_TABLE;
        String INSERT_INTO_BLACK_LIST_TABLE = appConfig.getDbType() == DBType.MYSQL ? QueriesConstants.INSERT_INTO_BLACK_LIST_TABLE : QueriesConstants.ORACLE_INSERT_INTO_BLACK_LIST_TABLE;
        String INSERT_INTO_BLACK_LIST_HIS_TABLE = appConfig.getDbType() == DBType.MYSQL ? QueriesConstants.INSERT_INTO_BLACK_LIST_HIS_TABLE : QueriesConstants.ORACLE_INSERT_INTO_BLACK_LIST_HIS_TABLE;
        String nowDate = appConfig.getDbType() == DBType.MYSQL ? "NOW()" : "sysdate";
        try {
            String dateTime = LocalDateTime.of(LocalDate.now().minusDays(systemConfigurationService.getStolenGreyToBlackListdays()), LocalTime.of(0, 0, 0, 0)).format(DateFormatterConstants.simpleDateFormat);
            queryExecutorService.execute(SELECT_GREY_LIST_EXIST_IN_STOLEN_TABLE.replaceAll(QueriesConstants.PARAM_DATE_TIME, dateTime).replaceAll(QueriesConstants.CURRENT_TIME, nowDate));
            queryExecutorService.execute(INSERT_INTO_GREY_HIS_TABLE.replaceAll(QueriesConstants.PARAM_OPERATION, String.valueOf(DeviceSyncOperation.DELETE.ordinal())));
            queryExecutorService.execute(INSERT_INTO_BLACK_LIST_TABLE);
            queryExecutorService.execute(INSERT_INTO_BLACK_LIST_HIS_TABLE.replaceAll(QueriesConstants.PARAM_OPERATION, String.valueOf(DeviceSyncOperation.ADD.ordinal())));
            Integer deletedCount = queryExecutorService.execute(QueriesConstants.DELETE_FROM_GREY_LIST_TABLE);
            queryExecutorService.execute(QueriesConstants.TRUNCATE_GREY_LIST_TEMP);

            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            logger.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), moduleName);
        } catch (Exception e) {
            logger.error("Error while running Queries ", e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(0);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }
}
