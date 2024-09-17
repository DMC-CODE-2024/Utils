package com.eirs.pairs.tempNationlaWhitelistreminder;

import com.eirs.pairs.alerts.AlertConfig;
import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.constants.*;
import com.eirs.pairs.dto.NotificationDetailsDto;
import com.eirs.pairs.repository.entity.ModuleAuditTrail;
import com.eirs.pairs.service.*;
import com.eirs.pairs.utils.DateFormatterConstants;
import com.eirs.pairs.utils.notification.dto.NotificationResponseDto;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class ReminderNotificationProcess {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    QueryExecutorService queryExecutorService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    SystemConfigurationService systemConfigurationService;

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;
    private String moduleName = UtilityType.REMINDER_UTILITY.name();
    private int failCount = 0;

    private int successCount = 0;

    @Autowired
    ModuleAlertService moduleAlertService;

    @Autowired
    AlertConfig alertConfig;

    public void process() {
        Long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(localDate, moduleName)) {
            log.info("Process:{} will not execute it may already Running or Completed for the day {}", moduleName, localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().moduleName(moduleName).featureName(moduleName).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(moduleName).featureName(moduleName).build();

        try {
            process(0, systemConfigurationService.getReminderFirstNotificationDays());
            process(1, systemConfigurationService.getReminderSecondNotificationDays());
            process(2, systemConfigurationService.getReminderThirdNotificationDays());
            updateModuleAuditTrail.setStatusCode(200);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), moduleName);
            log.error("Error while ReminderNotificationProcess Error:{} ", e.getMessage(), e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(0);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void process(Integer reminderStatus, Integer reminderDays) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(reminderDays);
        LocalDateTime endDate = startDate.plusDays(1);
        String startRange = startDate.format(DateFormatterConstants.simpleDateFormat);
        String endRange = endDate.format(DateFormatterConstants.simpleDateFormat);

        String query;
        if (appConfig.getDbType() == DBType.MYSQL) {
            query = ReminderQueriesConstants.SELECT_MYSQL_TEMP_NATIONAL_WHITELIST;
        } else if (appConfig.getDbType() == DBType.ORACLE) {
            query = ReminderQueriesConstants.SELECT_ORACLE_TEMP_NATIONAL_WHITELIST;
        } else {
            throw new RuntimeException("DB Type not support");
        }
        final String finalQuery = query.replaceAll(ReminderQueriesConstants.PARAM_REMINDER_STATUS, String.valueOf(reminderStatus))
                .replaceAll(ReminderQueriesConstants.PARAM_START_RANGE, startRange)
                .replaceAll(ReminderQueriesConstants.PARAM_END_RANGE, endRange);
        log.info("Reminder Notification executing Query for reminderStatus:{} reminderDays:{} Query:[{}]", reminderStatus, reminderDays, finalQuery);
        try {
            Connection connection = queryExecutorService.getJdbcTemplate().getDataSource().getConnection();
            try (Statement st = connection.createStatement();
                 ResultSet resultSet = st.executeQuery(finalQuery);) {
                log.info("No of row:{} query:[{}]", resultSet.getRow(), finalQuery);
                while (resultSet.next()) {
                    TempNationalWhiteDto dto = new TempNationalWhiteDto();
                    dto.setNationalWhitelistId(resultSet.getLong("NATIONAL_WHITELIST_ID"));
                    dto.setMsisdn(resultSet.getString("MSISDN"));
                    dto.setImsie(resultSet.getString("IMSI"));
                    dto.setImei(resultSet.getString("IMEI"));
                    dto.setActualImei(resultSet.getString("ACTUAL_IMEI"));
                    dto.setReminderStatus(resultSet.getInt("REMINDER_STATUS"));
                    dto.setActualOperator(resultSet.getString("ACTUAL_OPERATOR"));
                    dto.setMobileOperator(resultSet.getString("MOBILE_OPERATOR"));
                    dto.setCreatedOn(resultSet.getDate("CREATED_ON_DATE"));
                    log.info("Going to sent Reminder Notification for {}", dto);
                    sendNotification(dto, reminderStatus, reminderDays);
                }
            }
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), moduleName);
        } catch (Exception e) {
            log.error("Error while Getting connection Error:{}", e.getMessage(), e);
        }
    }

    private SmsTag getSmsTag(Integer reminderSatus) {
        SmsTag smsTag = null;
        if (reminderSatus == 0)
            smsTag = SmsTag.NationalWhitelistReminder1Sms;
        else if (reminderSatus == 1)
            smsTag = SmsTag.NationalWhitelistReminder2Sms;
        else smsTag = SmsTag.NationalWhitelistReminder3Sms;
        return smsTag;
    }

    private void sendNotification(TempNationalWhiteDto tempNationalWhiteDto, Integer reminderStatus, Integer reminderDays) {
        Map<SmsPlaceHolders, String> map = new HashMap<>();
        map.put(SmsPlaceHolders.IMEI, tempNationalWhiteDto.getImei());
        map.put(SmsPlaceHolders.DATE_DD_MMM_YYYY, DateFormatterConstants.reminderNotificationSmsDateFormat.format(tempNationalWhiteDto.getCreatedOn()));
        NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().msisdn(tempNationalWhiteDto.getMsisdn()).smsTag(getSmsTag(reminderStatus).name()).smsPlaceHolder(map).language(null).moduleName(alertConfig.getProcessId(UtilityType.REMINDER_UTILITY)).build();
        NotificationResponseDto responseDto = notificationService.sendSms(notificationDetailsDto);

        if (responseDto != null) {
            if ("SUCCESS".equalsIgnoreCase(responseDto.getMessage())) {
                successCount++;
                String query = ReminderQueriesConstants.UPDATE_NATIONAL_WHITELIST
                        .replaceAll(ReminderQueriesConstants.PARAM_REMINDER_STATUS, String.valueOf(tempNationalWhiteDto.getReminderStatus() + 1))
                        .replaceAll(ReminderQueriesConstants.PARAM_NATIONAL_WHITELIST_ID, String.valueOf(tempNationalWhiteDto.getNationalWhitelistId()));
                queryExecutorService.execute(query);
            } else {
                failCount++;
            }
        }
    }
}
