package com.eirs.pairs.pairMsisdnUpdate;

import com.eirs.pairs.alerts.AlertConfig;
import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.constants.SmsPlaceHolders;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.constants.pairing.GSMAStatus;
import com.eirs.pairs.dto.NotificationDetailsDto;
import com.eirs.pairs.exceptions.NotificationException;
import com.eirs.pairs.repository.DuplicateRepository;
import com.eirs.pairs.repository.HlrDumpRepository;
import com.eirs.pairs.repository.PairingRepository;
import com.eirs.pairs.repository.entity.Duplicate;
import com.eirs.pairs.repository.entity.HlrDumpEntity;
import com.eirs.pairs.repository.entity.ModuleAuditTrail;
import com.eirs.pairs.repository.entity.Pairing;
import com.eirs.pairs.service.*;
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
public class PairingUpdateMsisdnUtility implements UtilityService {

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

    @Override
    @Transactional
    public void runUtility() {
        String MODULE_NAME = appConfig.getModuleName(UtilityType.PAIR_UPDATE_MSISDN);
        Integer count = 0;
        Long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(localDate, MODULE_NAME)) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", MODULE_NAME, localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(MODULE_NAME).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(MODULE_NAME).build();
        try {
            List<Pairing> pairings = pairingRepository.findByMsisdnIsNull();
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
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), MODULE_NAME);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), MODULE_NAME);
            logger.error("Error while running Queries ", e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(count);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }

    private void sendNotifications(Pairing pairing) {
        try {
            logger.info("Sending Notification for Record {}", pairing);
            Map<SmsPlaceHolders, String> map = new HashMap<>();
            map.put(SmsPlaceHolders.ACTUAL_IMEI, pairing.getActualImei());
            map.put(SmsPlaceHolders.IMSI, pairing.getImsi());
            map.put(SmsPlaceHolders.MSISDN, pairing.getMsisdn());
            NotificationDetailsDto notificationDetailsDto = null;
            if (pairing.getGsmaStatus() == GSMAStatus.VALID) {
                notificationDetailsDto = NotificationDetailsDto.builder().msisdn(pairing.getMsisdn()).smsTag(SmsTag.AutoPairGsmaValidSMS.name()).smsPlaceHolder(map).language(null).moduleName(appConfig.getModuleName(UtilityType.PAIR_UPDATE_MSISDN)).build();
            } else {
                notificationDetailsDto = NotificationDetailsDto.builder().msisdn(pairing.getMsisdn()).smsTag(SmsTag.AutoPairGsmaInvalidSMS.name()).smsPlaceHolder(map).language(null).moduleName(appConfig.getModuleName(UtilityType.PAIR_UPDATE_MSISDN)).build();
            }
            notificationDetailsDto.setStartTime(systemConfigurationService.getPairingNotificationSmsStartTime());
            notificationDetailsDto.setEndTime(systemConfigurationService.getPairingNotificationSmsEndTime());
            notificationService.sendSmsInWindow(notificationDetailsDto);
        } catch (NotificationException e) {
            logger.error("Notification not send for duplicate:{} Error:{}", pairing, e.getMessage());
        }
    }
}

