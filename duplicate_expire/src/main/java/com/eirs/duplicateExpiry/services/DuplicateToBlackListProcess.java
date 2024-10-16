package com.eirs.duplicateExpiry.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.DateFormatterConstants;
import com.eirs.duplicateExpiry.mapper.PairingMapper;
import com.eirs.duplicateExpiry.repository.ImeiPairDetailHisRepository;
import com.eirs.duplicateExpiry.repository.entity.ExceptionList;
import com.eirs.duplicateExpiry.repository.entity.ImeiPairDetailHis;
import com.eirs.duplicateExpiry.repository.entity.Pairing;
import com.eirs.model.ModuleAuditTrail;
import com.eirs.services.ModuleAlertService;
import com.eirs.services.ModuleAuditTrailService;
import com.eirs.services.QueryExecutorService;
import org.mapstruct.factory.Mappers;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DuplicateToBlackListProcess {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BlackListService blackListService;

    private final PairingMapper mapper = Mappers.getMapper(PairingMapper.class);
    @Autowired
    PairingService pairingService;

    @Autowired
    ImeiPairDetailHisRepository imeiPairDetailHisRepository;

    @Autowired
    ExceptionListService exceptionListService;

    @Autowired
    QueryExecutorService queryExecutorService;

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;
    AtomicInteger counter = new AtomicInteger(0);
    @Autowired
    AppConfig appConfig;
    final String updateDuplicateQuery = "update duplicate_device_detail set status='BLOCK' where id=<ID>";

    @Autowired
    ModuleAlertService moduleAlertService;

    final String moduleName = "duplicate";

    public void runUtility() {
        long start = System.currentTimeMillis();
        LocalDate localDate = LocalDate.now();
        if (!moduleAuditTrailService.canProcessRun(localDate, appConfig.getFeatureName())) {
            log.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(moduleName).featureName(appConfig.getFeatureName()).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(moduleName).featureName(appConfig.getFeatureName()).build();
        String dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0, 0)).format(DateFormatterConstants.simpleDateFormat);
        String finalQuery = "select * from duplicate_device_detail where expiry_date < '" + dateTime + "' and status='DUPLICATE'";
        log.info("Going to select data by query:[{}]", finalQuery);
        try {
            Connection connection = queryExecutorService.getJdbcTemplate().getDataSource().getConnection();
            try (Statement st = connection.createStatement();
                 ResultSet resultSet = st.executeQuery(finalQuery);) {
                log.info("No of row:{} query:[{}]", resultSet.getRow(), finalQuery);
                while (resultSet.next()) {
                    DuplicateDto duplicateDto = new DuplicateDto();
                    duplicateDto.setId(resultSet.getLong("id"));
                    duplicateDto.setImsie(resultSet.getString("imsi"));
                    duplicateDto.setActualImei(resultSet.getString("actual_imei"));
                    duplicateDto.setImei(resultSet.getString("imei"));
                    duplicateDto.setOperator(resultSet.getString("operator"));
                    duplicateDto.setMsisdn(resultSet.getString("msisdn"));
                    log.info("Going to add this to Blacklist {}", duplicateDto);
                    process(duplicateDto);
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
    private void process(DuplicateDto duplicateDto) {
        validatePair(duplicateDto);
        validateException(duplicateDto);
        blackListService.addAndUpdate(duplicateDto);
    }

    private void validatePair(DuplicateDto duplicateDto) {
        Pairing pairing = pairingService.getByImeiAndImsie(duplicateDto.getImei(), duplicateDto.getImsie());
        if (pairing != null) {
            pairingService.delete(pairing);
            ImeiPairDetailHis imeiPairDetailHis = mapper.toImeiPairDetailHis(pairing);
            imeiPairDetailHis.setAction("DELETE");
            imeiPairDetailHis.setActionRemark("DUPLICATE");
            log.info("Going to Save to imei_pair_detail_his:{} ", imeiPairDetailHis);
            imeiPairDetailHis = imeiPairDetailHisRepository.save(imeiPairDetailHis);
            log.info("Saved to imei_pair_detail_his:{} ", imeiPairDetailHis);
        }
    }

    private void validateException(DuplicateDto duplicateDto) {
        ExceptionList exceptionList = exceptionListService.getNotVIPImeiAndImsi(duplicateDto.getImei(), duplicateDto.getImsie());
        if (exceptionList != null) {
            exceptionListService.delete(duplicateDto);
        }
    }
}
