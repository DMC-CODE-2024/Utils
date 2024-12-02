package com.eirs.audit.orchestrator;

import com.eirs.audit.constant.AuditSystemConfigKeys;
import com.eirs.audit.constant.DateFormatterConstants;
import com.eirs.audit.model.EirsData;
import com.eirs.audit.services.AuditProcess;
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


    public void processAudit(LocalDate localDate) {
        Long start = System.currentTimeMillis();
        if (!moduleAuditTrailService.canProcessRun(localDate, appConfig.getFeatureName())) {
            logger.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        try {
            for (String operator : systemConfigurationService.getOperators()) {
                Integer noOfEirs = systemConfigurationService.getNoOfEirs(operator);
                LocalDateTime startDate = LocalDateTime.of(localDate, LocalTime.of(0, 0));
                LocalDateTime endDate = LocalDateTime.of(localDate, LocalTime.of(0, 0));
                for (int i = 1; i <= noOfEirs; i++) {
                    String operatorFilePath = systemConfigurationService.findByKey(AuditSystemConfigKeys.OPERATOR_FILE_PATH.replaceAll("<OPERATOR>", operator).replaceAll("<NUMBER>", String.valueOf(i)));
                    String operatorEirFilename = operatorFilePath + "/EIR_" + operator.toUpperCase() + "_" + DateFormatterConstants.eirFilePreDateFormat.format(startDate) + ".csv";
                    List<EirsData> exceptionListMissingRecords = exceptionListAuditProcess.process(operatorEirFilename, startDate, endDate, operator);
                    List<EirsData> greyListMissingRecords = greyListAuditProcess.process(operatorEirFilename, startDate, endDate, operator);
                    List<EirsData> blackListMissingRecords = blackListAuditProcess.process(operatorEirFilename, startDate, endDate, operator);
                    List<EirsData> blockedTacMissingRecords = blockedTacAuditProcess.process(operatorEirFilename, startDate, endDate, operator);
                }
            }
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
