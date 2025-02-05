package com.eirs.pairClean.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.DBType;
import com.eirs.constants.DateFormatterConstants;
import com.eirs.constants.SmsPlaceHolders;
import com.eirs.constants.SmsTag;
import com.eirs.model.ModuleAuditTrail;
import com.eirs.services.ModuleAlertService;
import com.eirs.services.ModuleAuditTrailService;
import com.eirs.services.NotificationService;
import com.eirs.services.QueryExecutorService;
import com.eirs.utils.notification.dto.NotificationDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PairCleanUpProcess {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    QueryExecutorService queryExecutorService;

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;
    AtomicInteger counter = new AtomicInteger(0);
    @Autowired
    AppConfig appConfig;
    @Autowired
    NotificationService notificationService;
    @Autowired
    SystemConfigurationService systemConfigurationService;
    @Autowired
    ModuleAlertService moduleAlertService;

    final String MODULE_NAME = "manual_pairing";

    public void runUtility() {
        long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(localDate, appConfig.getFeatureName())) {
            log.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        String selectQuery = appConfig.getDbType() == DBType.MYSQL ? PairCleanQueriesConstants.SELECT_PAIR_MYSQL : PairCleanQueriesConstants.SELECT_PAIR_ORACLE;
        String startRange = LocalDateTime.of(LocalDate.now().minusDays(systemConfigurationService.getManualPairCleanUpDays()), LocalTime.of(0, 0)).format(DateFormatterConstants.simpleDateFormat);
        String finalQuery = selectQuery.replaceAll(PairCleanQueriesConstants.PARAM_START_RANGE, startRange);
        try {
            Connection connection = queryExecutorService.getJdbcTemplate().getDataSource().getConnection();
            try (Statement st = connection.createStatement(); ResultSet resultSet = st.executeQuery(finalQuery);) {
                log.info("No of row:{} query:[{}]", resultSet.getRow(), finalQuery);
                while (resultSet.next()) {
                    deleteAndAdd(resultSet.getLong("id"));
                    String imsi = resultSet.getString("imsi");
                    String actualImei = resultSet.getString("actual_imei");
                    String imei = resultSet.getString("imei");
                    String msisdn = resultSet.getString("msisdn");
                    sendNotifications(actualImei, imsi, msisdn);
                    counter.getAndIncrement();
                }
            }
            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), appConfig.getFeatureName());
            updateModuleAuditTrail.setStatusCode(500);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), appConfig.getFeatureName());
            log.error("Error while Getting connection Error:{}", e.getMessage(), e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(counter.get());
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void deleteAndAdd(Long id) {
        String insertHistory = appConfig.getDbType() == DBType.MYSQL ? PairCleanQueriesConstants.MYSQL_INSERT_INTO_HISTORY : PairCleanQueriesConstants.ORACLE_INSERT_INTO_HISTORY;
        String pairCleanQuery = appConfig.getDbType() == DBType.MYSQL ? PairCleanQueriesConstants.MYSQL_DELETE_PAIRING_TABLE : PairCleanQueriesConstants.ORACLE_DELETE_PAIRING_TABLE;
        queryExecutorService.execute(insertHistory.replaceAll(PairCleanQueriesConstants.PARAM_ID, String.valueOf(id)));
        queryExecutorService.execute(pairCleanQuery.replaceAll(PairCleanQueriesConstants.PARAM_ID, String.valueOf(id)));
    }

    private void sendNotifications(String actualImei, String msisdn, String imsi) {
        try {
            log.info("Sending Notification for actualImei:{} , msisdn:{} , imei:{}", actualImei, msisdn, imsi);
            Map<SmsPlaceHolders, String> map = new HashMap<>();
            map.put(SmsPlaceHolders.ACTUAL_IMEI, actualImei);
            map.put(SmsPlaceHolders.IMSI, imsi);
            map.put(SmsPlaceHolders.MSISDN, msisdn);
            NotificationDetailsDto notificationDetailsDto = com.eirs.utils.notification.dto.NotificationDetailsDto.builder().msisdn(msisdn).smsTag(SmsTag.PairCleanUpSms.name()).smsPlaceHolder(map).language(systemConfigurationService.getDefaultLanguage()).moduleName(appConfig.getFeatureName()).build();
            notificationDetailsDto.setStartTime(systemConfigurationService.getPairingNotificationSmsStartTime());
            notificationDetailsDto.setEndTime(systemConfigurationService.getPairingNotificationSmsEndTime());
            notificationService.sendSmsInWindow(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification not send for  actualImei:{} , msisdn:{} , imei:{} Error:{}", actualImei, msisdn, imsi, e.getMessage());
        }
    }
}
