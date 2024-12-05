package com.eirs.audit.orchestrator;

import com.eirs.audit.constant.AuditSystemConfigKeys;
import com.eirs.audit.constant.DateFormatterConstants;
import com.eirs.audit.mapper.AuditTableMapper;
import com.eirs.audit.model.EirsData;
import com.eirs.audit.services.AuditProcess;
import com.eirs.audit.services.EirlistOutputAuditService;
import com.eirs.audit.services.SystemConfigurationService;
import com.eirs.config.AppConfig;
import com.eirs.model.ModuleAuditTrail;
import com.eirs.services.ModuleAlertService;
import com.eirs.services.ModuleAuditTrailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditOrchestrator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;
    @Autowired
    AppConfig appConfig;

    final String MODULE_NAME = "eir_validator";

    @Autowired
    private ModuleAlertService moduleAlertService;


    @Autowired
    @Qualifier("ExceptionListAuditProcess")
    private AuditProcess exceptionListAuditProcess;

    @Autowired
    @Qualifier("BlackListAuditProcess")
    private AuditProcess blackListAuditProcess;

    @Autowired
    @Qualifier("GreyListAuditProcess")
    private AuditProcess greyListAuditProcess;

    @Autowired
    @Qualifier("BlockedTacAuditProcess")
    private AuditProcess blockedTacAuditProcess;

    @Autowired
    private EirlistOutputAuditService eirlistOutputAuditService;

    public void processAudit(LocalDate localDate) {
        Long start = System.currentTimeMillis();
        if (!moduleAuditTrailService.canProcessRun(localDate, appConfig.getFeatureName())) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        try {
            List<EirsData> finalMissingRecords = new ArrayList<>();
            for (String operator : systemConfigurationService.getOperators()) {
                Integer noOfEirs = systemConfigurationService.getNoOfEirs(operator);
                LocalDateTime startDate = LocalDateTime.of(localDate, LocalTime.of(0, 0));
                LocalDateTime endDate = LocalDateTime.of(localDate, LocalTime.of(0, 0));
                for (int i = 1; i <= noOfEirs; i++) {
                    finalMissingRecords.addAll(exceptionListAuditProcess.process(i, startDate, endDate, operator));
                    finalMissingRecords.addAll(greyListAuditProcess.process(i, startDate, endDate, operator));
                    finalMissingRecords.addAll(blackListAuditProcess.process(i, startDate, endDate, operator));
                    finalMissingRecords.addAll(blockedTacAuditProcess.process(i, startDate, endDate, operator));
                }
            }
            logger.info("finalMissingRecords size:{}", finalMissingRecords.size());
            if (finalMissingRecords.size() > 0)
                eirlistOutputAuditService.save(AuditTableMapper.toEntity(finalMissingRecords));
            updateModuleAuditTrail.setStatusCode(200);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), appConfig.getFeatureName());
            logger.error("Error while running Process ", e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(0);
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }
}
