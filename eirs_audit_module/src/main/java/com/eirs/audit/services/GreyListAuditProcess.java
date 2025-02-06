package com.eirs.audit.services;

import com.eirs.audit.constant.AuditSystemConfigKeys;
import com.eirs.audit.constant.DateFormatterConstants;
import com.eirs.audit.constant.FileType;
import com.eirs.audit.model.EirsData;
import com.eirs.audit.repository.entity.EirlistOutputAuditConstants;
import com.eirs.audit.util.FileNameUtil;
import com.eirs.config.AppConfig;
import com.eirs.services.ModuleAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service("GreyListAuditProcess")
public class GreyListAuditProcess implements AuditProcess {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    String filePrefix = "TRACKEDLIST";

    @Value("${files.path}")
    private String filePath;
    @Autowired
    private SystemConfigurationService systemConfigurationService;
    @Autowired
    AppConfig appConfig;
    @Autowired
    private ModuleAlertService moduleAlertService;
    private Map<EirsData, Boolean> eirsDataSet = new HashMap<>();

    public void fillEirsData(String operator, LocalDateTime startDate, LocalDateTime endDate) {
        eirsDataSet.clear();
        String shortCode = systemConfigurationService.getShortCode(operator);
        String filename = FileNameUtil.getFilename(startDate, endDate, FileType.DAILY_FULL, filePrefix, shortCode);
        String filepath = filePath + "/" + operator.toLowerCase() + "/" + FileType.DAILY_FULL.getValue() + "/";
        logger.info("Reading file EIRs file:{}", (filepath + filename));
        try (Stream<String> stream = Files.lines(Paths.get(filepath + filename))) {
            stream.skip(1).forEach(data -> {
                eirsDataSet.put(new EirsData(data, EirlistOutputAuditConstants.GREY_LIST_NAME, EirlistOutputAuditConstants.EIRS_SOURCE, operator, filename, 0), Boolean.TRUE);
            });
            logger.info("Reading file EIRs file:{} Total Count:{}", (filepath + filename), eirsDataSet.size());
        } catch (Exception e) {
            logger.error("Exception while Reading filepath:{} filename:{} Error:{}", filepath, filename, e.getMessage());
            moduleAlertService.sendAuditFileNotFoundAlert(filename, operator, 0, appConfig.getFeatureName());
        }
    }

    public List<EirsData> process(Integer eirNumber, LocalDateTime startDate, LocalDateTime endDate, String operator) {
        logger.info("Process starting for ListName:{}", filePrefix);
        String operatorFilePath = systemConfigurationService.findByKey(AuditSystemConfigKeys.OPERATOR_FILE_PATH.replaceAll("<OPERATOR>", operator).replaceAll("<NUMBER>", String.valueOf(eirNumber)));
        String operatorEirFilename = "EIR_" + filePrefix + "_" + operator.toUpperCase() + "_" + DateFormatterConstants.eirFilePreDateFormat.format(startDate) + ".csv";
        List<EirsData> missingRecords = new ArrayList<>();
        fillEirsData(operator, startDate, endDate);
        Map<EirsData, Boolean> eirDataSet = new HashMap<>();
        logger.info("Reading file EIR operatorEirFilename:{}", (operatorFilePath + "/" + operatorEirFilename));
        try (Stream<String> stream = Files.lines(Paths.get(operatorFilePath + "/" + operatorEirFilename))) {
            stream.skip(1).forEach(data -> eirDataSet.put(new EirsData(data, EirlistOutputAuditConstants.GREY_LIST_NAME, EirlistOutputAuditConstants.EIR_SOURCE, operator, operatorEirFilename, eirNumber), Boolean.TRUE));
            eirDataSet.forEach((eirData, value) -> {
                if (!eirsDataSet.containsKey(eirData)) {
                    missingRecords.add(eirData);
                }
            });
            eirsDataSet.forEach((eirData, value) -> {
                if (!eirDataSet.containsKey(eirData)) {
                    missingRecords.add(eirData);
                }
            });
            logger.info("missingRecords:{} operator:{} ListName:{}", missingRecords.size(), operator, filePrefix);
        } catch (Exception e) {
            logger.error("Exception while Reading operatorEirFilename:{} Error:{}", operatorEirFilename, e.getMessage());
            moduleAlertService.sendAuditFileNotFoundAlert(operatorEirFilename, operator, eirNumber, appConfig.getFeatureName());
        }
        return missingRecords;
    }


}
