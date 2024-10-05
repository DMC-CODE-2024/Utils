package com.eirs.pairs.reminder;

import com.eirs.pairs.alerts.AlertConfig;
import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.constants.DBType;
import com.eirs.pairs.constants.SmsPlaceHolders;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.dto.NotificationDetailsDto;
import com.eirs.pairs.exceptions.NotificationException;
import com.eirs.pairs.repository.entity.ModuleAuditTrail;
import com.eirs.pairs.service.*;
import com.eirs.pairs.utils.DateFormatterConstants;
import com.eirs.pairs.utils.notification.dto.NotificationResponseDto;
import org.apache.commons.lang3.StringUtils;
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

@Service
public class GenericReminderNotificationProcess {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    QueryExecutorService queryExecutorService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    SystemConfigurationService systemConfigurationService;
    private int failCount = 0;

    private int successCount = 0;

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;
    @Autowired
    private ModuleAlertService moduleAlertService;

    private String moduleName = UtilityType.REMINDER_UTILITY.name();
    private String featureName = UtilityType.REMINDER_UTILITY.name();
    public static String processName = null;

    private String ID = "ID";

    private String NWL_ID = "national_whitelist_id";

    public void process() {
        ID = StringUtils.equalsIgnoreCase("nationalWhiteList", processName) ? NWL_ID : ID;
        featureName = moduleName + "_" + processName;
        Long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(localDate, featureName)) {
            log.info("Process:{} will not execute it may already Running or Completed for the day {}", featureName, localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(moduleName).featureName(featureName).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(moduleName).featureName(featureName).build();
        try {
            process(0, systemConfigurationService.getGenericReminderFirstNotificationDays());
            process(1, systemConfigurationService.getGenericReminderSecondNotificationDays());
            process(2, systemConfigurationService.getGenericReminderThirdNotificationDays());
            updateModuleAuditTrail.setStatusCode(200);
        } catch (Exception e) {
            log.error("Error while GenericNotificationProcess Error:{} ", e.getMessage(), e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(0);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void process(Integer reminderStatus, Integer reminderDays) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(reminderDays).withMinute(0).withHour(0).withSecond(0);
        LocalDateTime endDate = startDate.plusDays(1);
        String startRange = startDate.format(DateFormatterConstants.simpleDateFormat);
        String endRange = endDate.format(DateFormatterConstants.simpleDateFormat);

        String query;
        if (appConfig.getDbType() == DBType.MYSQL) {
            query = GenericReminderQueriesConstants.SELECT_MYSQL_REMINDER_TABLE;
        } else if (appConfig.getDbType() == DBType.ORACLE) {
            query = GenericReminderQueriesConstants.SELECT_ORACLE_REMINDER_TABLE;
        } else {
            throw new RuntimeException("DB Type not support");
        }
        final String finalQuery = query.replaceAll(GenericReminderQueriesConstants.PARAM_REMINDER_STATUS, String.valueOf(reminderStatus))
                .replaceAll(GenericReminderQueriesConstants.PARAM_START_RANGE, startRange)
                .replaceAll(GenericReminderQueriesConstants.PARAM_END_RANGE, endRange)
                .replaceAll(GenericReminderQueriesConstants.TABLE_NAME, systemConfigurationService.getGenericReminderTableName()).
                replaceAll(GenericReminderQueriesConstants.WHERE_CLAUSE, systemConfigurationService.getGenericReminderWhereClause());
        log.info("Generic Notification executing Query for reminderStatus:{} reminderDays:{} Query:[{}]", reminderStatus, reminderDays, finalQuery);
        try {
            Connection connection = queryExecutorService.getJdbcTemplate().getDataSource().getConnection();
            try (Statement st = connection.createStatement();
                 ResultSet resultSet = st.executeQuery(finalQuery);) {
                log.info("No of row:{} query:[{}]", resultSet.getRow(), finalQuery);
                while (resultSet.next()) {
                    GenericTableDto dto = new GenericTableDto();
                    dto.setId(resultSet.getLong(ID));
                    dto.setMsisdn(resultSet.getString("MSISDN"));
                    dto.setImsie(resultSet.getString("IMSI"));
                    dto.setImei(resultSet.getString("IMEI"));
                    dto.setActualImei(resultSet.getString("ACTUAL_IMEI"));
                    dto.setReminderStatus(resultSet.getInt("REMINDER_STATUS"));
                    dto.setTransactionId(resultSet.getString("transaction_id"));
                    dto.setCreatedOn(resultSet.getDate("CREATED_ON"));
                    log.info("Going to sent Generic Notification for {}", dto);
                    sendNotification(dto, reminderStatus, reminderDays);
                }
            }
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), featureName);
        } catch (Exception e) {
            log.error("Error while Getting connection Error:{}", e.getMessage(), e);
        }
    }

    private String getSmsTag(Integer reminderSatus) {
        SmsTag smsTag = null;
        if (reminderSatus == 0)
            smsTag = SmsTag.GenericReminder1Sms;
        else if (reminderSatus == 1)
            smsTag = SmsTag.GenericReminder2Sms;
        else smsTag = SmsTag.GenericReminder3Sms;
        return processName + "_" + smsTag;
    }

    private void sendNotification(GenericTableDto tempNationalWhiteDto, Integer reminderStatus, Integer reminderDays) {
        Map<SmsPlaceHolders, String> map = new HashMap<>();
        map.put(SmsPlaceHolders.ACTUAL_IMEI, tempNationalWhiteDto.getActualImei());
        map.put(SmsPlaceHolders.IMEI, tempNationalWhiteDto.getImei());
        map.put(SmsPlaceHolders.IMSI, tempNationalWhiteDto.getImsie());
        map.put(SmsPlaceHolders.MSISDN, tempNationalWhiteDto.getMsisdn());
        map.put(SmsPlaceHolders.REQUEST_ID, tempNationalWhiteDto.getTransactionId());
        map.put(SmsPlaceHolders.DATE_DD_MMM_YYYY, DateFormatterConstants.reminderNotificationSmsDateFormat.format(tempNationalWhiteDto.getCreatedOn()));
        NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().msisdn(tempNationalWhiteDto.getMsisdn()).smsTag(getSmsTag(reminderStatus)).smsPlaceHolder(map).language(null).moduleName(appConfig.getModuleName(UtilityType.REMINDER_UTILITY)).requestId(tempNationalWhiteDto.getTransactionId()).build();
        notificationDetailsDto.setStartTime(systemConfigurationService.getNotificationSmsStartTime(appConfig.getModuleName(UtilityType.REMINDER_UTILITY)));
        notificationDetailsDto.setEndTime(systemConfigurationService.getNotificationSmsEndTime(appConfig.getModuleName(UtilityType.REMINDER_UTILITY)));
        try {
            NotificationResponseDto responseDto = notificationService.sendSmsInWindow(notificationDetailsDto);
            if (responseDto != null) {
                if ("SUCCESS".equalsIgnoreCase(responseDto.getMessage())) {
                    successCount++;
                    String query = GenericReminderQueriesConstants.UPDATE_NATIONAL_WHITELIST
                            .replaceAll(GenericReminderQueriesConstants.WHERE_ID, ID)
                            .replaceAll(GenericReminderQueriesConstants.PARAM_REMINDER_STATUS, String.valueOf(tempNationalWhiteDto.getReminderStatus() + 1))
                            .replaceAll(GenericReminderQueriesConstants.ID, String.valueOf(tempNationalWhiteDto.getId()))
                            .replaceAll(GenericReminderQueriesConstants.TABLE_NAME, systemConfigurationService.getGenericReminderTableName());
                    queryExecutorService.execute(query);
                } else {
                    failCount++;
                }
            }
        } catch (NotificationException e) {
            log.info("Notification not sent for GenericTableDto:{}", tempNationalWhiteDto);
        }

    }
}
