package com.eirs.updateDuplicateMsisdn.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.SmsPlaceHolders;
import com.eirs.constants.SmsTag;
import com.eirs.exception.NotificationException;
import com.eirs.model.ModuleAuditTrail;
import com.eirs.services.ModuleAlertService;
import com.eirs.services.ModuleAuditTrailService;
import com.eirs.services.NotificationService;
import com.eirs.updateDuplicateMsisdn.repository.DuplicateRepository;
import com.eirs.updateDuplicateMsisdn.repository.HlrDumpRepository;
import com.eirs.updateDuplicateMsisdn.repository.entity.Duplicate;
import com.eirs.updateDuplicateMsisdn.repository.entity.HlrDumpEntity;
import com.eirs.utils.notification.dto.NotificationDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DuplicateUpdateMsisdnProcess {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NotificationService notificationService;
    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    DateTimeFormatter notificationSmsDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private DuplicateRepository duplicateRepository;

    @Autowired
    private HlrDumpRepository hlrDumpRepository;

    @Autowired
    private SystemConfigurationService systemConfigurationService;
    @Autowired
    AppConfig appConfig;

    final String MODULE_NAME = "Duplicate";

    @Transactional
    public void runUtility() {
        Integer count = 0;
        Long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(localDate, appConfig.getFeatureName())) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        try {
            List<Duplicate> duplicates = duplicateRepository.findByMsisdnIsNullOrMsisdn("");
            if (!CollectionUtils.isEmpty(duplicates)) {
                for (Duplicate duplicate : duplicates) {
                    HlrDumpEntity hlrDump = hlrDumpRepository.findByImsi(duplicate.getImsie());
                    if (hlrDump == null) {
                        logger.info("Not found HLR Data for IMSI:{} for {}", duplicate.getImsie(), duplicate);
                    } else {
                        duplicate.setMsisdn(hlrDump.getMsisdn());
                        sendNotifications(duplicate);
                        duplicateRepository.save(duplicate);
                    }
                }
            }
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
        updateModuleAuditTrail.setCount(count);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }

    private void sendNotifications(Duplicate duplicate) {
        try {
            logger.info("Sending Notification for Record {}", duplicate);
            Map<SmsPlaceHolders, String> map = new HashMap<>();
            map.put(SmsPlaceHolders.ACTUAL_IMEI, duplicate.getActualImei());
            map.put(SmsPlaceHolders.IMSI, duplicate.getImsie());
            map.put(SmsPlaceHolders.OPERATOR, duplicate.getOperator());
            map.put(SmsPlaceHolders.IMSI, duplicate.getImsie());
            map.put(SmsPlaceHolders.IMEI, duplicate.getImei());
            map.put(SmsPlaceHolders.REQUEST_ID, duplicate.getTransactionId());
            map.put(SmsPlaceHolders.MSISDN, duplicate.getMsisdn());
            map.put(SmsPlaceHolders.DATE_DD_MMM_YYYY, notificationSmsDateFormat.format(duplicate.getExpiryDate()));
            NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().msisdn(duplicate.getMsisdn()).smsTag(SmsTag.DuplicateSms.name()).smsPlaceHolder(map).language(systemConfigurationService.getDefaultLanguage()).moduleName(appConfig.getFeatureName()).build();
            notificationDetailsDto.setStartTime(systemConfigurationService.getDuplicateNotificationSmsStartTime());
            notificationDetailsDto.setEndTime(systemConfigurationService.getDuplicateNotificationSmsEndTime());
            notificationService.sendSmsInWindow(notificationDetailsDto);
        } catch (NotificationException e) {
            logger.error("Notification not send for duplicate:{} Error:{}", duplicate, e.getMessage());
        }
    }
}

