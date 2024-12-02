package com.eirs.audit.services;

import com.eirs.audit.constant.FileType;
import com.eirs.audit.model.EirsData;
import com.eirs.audit.repository.entity.EirlistOutputAuditConstants;
import com.eirs.audit.util.FileNameUtil;
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

    private Map<EirsData, Boolean> eirsDataSet = new HashMap<>();

    public void fillEirsData(String operator, LocalDateTime startDate, LocalDateTime endDate) {
        if (eirsDataSet.isEmpty()) {
            String shortCode = systemConfigurationService.getShortCode(operator);
            String filename = FileNameUtil.getFilename(startDate, endDate, FileType.DAILY_FULL, filePrefix, shortCode);
            String filepath = filePath + "/" + operator.toLowerCase() + "/" + FileType.DAILY_FULL.getValue() + "/";
            try (Stream<String> stream = Files.lines(Paths.get(filepath + filename))) {
                stream.skip(1).forEach(data -> {
                    eirsDataSet.put(new EirsData(data, EirlistOutputAuditConstants.GREY_LIST_NAME, EirlistOutputAuditConstants.EIRS_SOURCE), Boolean.TRUE);
                });

            } catch (Exception e) {
                logger.error("Exception while Reading filepath:{} filename:{}", filepath, filename, e);
//                throw new RuntimeException(e);
            }
        }
    }

    public List<EirsData> process(String operatorEirFilename, LocalDateTime startDate, LocalDateTime endDate, String operator) {
        List<EirsData> missingRecords = new ArrayList<>();
        fillEirsData(operator, startDate, endDate);
        Map<EirsData, Boolean> eirDataSet = new HashMap<>();
        Integer row = 0;
        try (Stream<String> stream = Files.lines(Paths.get(operatorEirFilename))) {
            stream.skip(1).forEach(data -> eirDataSet.put(new EirsData(data, EirlistOutputAuditConstants.GREY_LIST_NAME, EirlistOutputAuditConstants.EIR_SOURCE), Boolean.TRUE));
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

        } catch (Exception e) {
            logger.error("Exception while Reading operatorEirFilename:{}", operatorEirFilename, e);
//            throw new RuntimeException(e);
        }
        return missingRecords;
    }


}
