package com.eirs.pairUpdateMsisdn.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.SmsPlaceHolders;
import com.eirs.constants.SmsTag;
import com.eirs.constants.pairing.GSMAStatus;
import com.eirs.exception.NotificationException;
import com.eirs.model.ModuleAuditTrail;
import com.eirs.pairUpdateMsisdn.repository.HlrDumpRepository;
import com.eirs.pairUpdateMsisdn.repository.PairingRepository;
import com.eirs.pairUpdateMsisdn.repository.entity.HlrDumpEntity;
import com.eirs.pairUpdateMsisdn.repository.entity.Pairing;
import com.eirs.services.ModuleAlertService;
import com.eirs.services.ModuleAuditTrailService;
import com.eirs.services.NotificationService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PairingUpdateMsisdnProcess {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NotificationService notificationService;
    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private PairingRepository pairingRepository;

    @Autowired
    private HlrDumpRepository hlrDumpRepository;

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @Autowired
    AppConfig appConfig;

    final String MODULE_NAME = "auto_pairing";

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
            List<Pairing> pairings = pairingRepository.findByMsisdnIsNullOrMsisdn("");
            if (!CollectionUtils.isEmpty(pairings)) {
                for (Pairing pairing : pairings) {
                    HlrDumpEntity hlrDump = hlrDumpRepository.findByImsi(pairing.getImsi());
                    if (hlrDump == null) {
                        logger.info("Not found HLR Data for IMSI:{} for {}", pairing.getImsi(), pairing);
                    } else {
                        pairing.setMsisdn(hlrDump.getMsisdn());
                        sendNotifications(pairing);
                        pairingRepository.save(pairing);
                    }
                }
            }
            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            logger.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), appConfig.getFeatureName());
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), appConfig.getFeatureName());
            logger.error("Error while running Queries ", e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(count);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }

    private void sendNotifications(Pairing pairing) {
        if (systemConfigurationService.sendPairingNotificationFlag()) {
            try {
                logger.info("Sending Notification for Record {}", pairing);
                Map<SmsPlaceHolders, String> map = new HashMap<>();
                map.put(SmsPlaceHolders.ACTUAL_IMEI, pairing.getActualImei());
                map.put(SmsPlaceHolders.IMSI, pairing.getImsi());
                map.put(SmsPlaceHolders.MSISDN, pairing.getMsisdn());
                NotificationDetailsDto notificationDetailsDto = null;
                if (pairing.getGsmaStatus() == GSMAStatus.VALID) {
                    notificationDetailsDto = NotificationDetailsDto.builder().msisdn(pairing.getMsisdn()).smsTag(SmsTag.AutoPairGsmaValidSMS.name()).smsPlaceHolder(map).language(systemConfigurationService.getDefaultLanguage()).moduleName(appConfig.getFeatureName()).build();
                } else {
                    notificationDetailsDto = NotificationDetailsDto.builder().msisdn(pairing.getMsisdn()).smsTag(SmsTag.AutoPairGsmaInvalidSMS.name()).smsPlaceHolder(map).language(systemConfigurationService.getDefaultLanguage()).moduleName(appConfig.getFeatureName()).build();
                }
                notificationDetailsDto.setStartTime(systemConfigurationService.getPairingNotificationSmsStartTime());
                notificationDetailsDto.setEndTime(systemConfigurationService.getPairingNotificationSmsEndTime());
                notificationService.sendSmsInWindow(notificationDetailsDto);
            } catch (NotificationException e) {
                logger.error("Notification not send for duplicate:{} Error:{}", pairing, e.getMessage());
            }
        }
    }
}

