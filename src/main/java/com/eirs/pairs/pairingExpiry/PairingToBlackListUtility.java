package com.eirs.pairs.pairingExpiry;

import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.constants.pairing.PairMode;
import com.eirs.pairs.constants.pairing.SyncStatus;
import com.eirs.pairs.mapper.PairingMapper;
import com.eirs.pairs.repository.ImeiPairDetailHisRepository;
import com.eirs.pairs.repository.entity.ImeiPairDetailHis;
import com.eirs.pairs.repository.entity.ModuleAuditTrail;
import com.eirs.pairs.repository.entity.Pairing;
import com.eirs.pairs.service.*;
import com.eirs.pairs.utils.DateFormatterConstants;
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
public class PairingToBlackListUtility implements UtilityService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BlackListService blackListService;

    @Autowired
    PairingExpiryRecordMapper recordMapper;
    @Autowired
    QueryExecutorService queryExecutorService;
    @Autowired
    ModuleAlertService moduleAlertService;

    final String deletePairingQuery = "delete from imei_pair_detail where id=<ID>";

    private final PairingMapper mapper = Mappers.getMapper(PairingMapper.class);

    @Autowired
    ImeiPairDetailHisRepository imeiPairDetailHisRepository;

    AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    private final String moduleName = UtilityType.PAIRING_EXPIRY_PROCESS.name();

    @Override
    public void runUtility() {
        LocalDate localDate = LocalDate.now();
        String dateTime = LocalDateTime.of(localDate, LocalTime.of(0, 0, 0, 0)).format(DateFormatterConstants.simpleDateFormat);
        if (!moduleAuditTrailService.canProcessRun(localDate, moduleName)) {
            log.info("Process:{} will not execute it may already Running or Completed for the day {}", moduleName, localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().moduleName(moduleName).featureName(moduleName).build());
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(moduleName).featureName(moduleName).build();
        long start = System.currentTimeMillis();
        String finalQuery = "select a.* from imei_pair_detail a where a.expiry_date < '" + dateTime + "'";
        log.info("Going to select data by query:[{}]", finalQuery);
        try {
            Connection connection = queryExecutorService.getJdbcTemplate().getDataSource().getConnection();
            try (Statement st = connection.createStatement();
                 ResultSet resultSet = st.executeQuery(finalQuery)) {
                log.info("No of row:{} query:[{}]", resultSet.getRow(), finalQuery);
                while (resultSet.next()) {
                    Pairing pairing = recordMapper.dbToPairing(resultSet);
                    log.info("Going to add this to Blacklist {}", pairing);
                    process(pairing);
                    counter.getAndIncrement();
                }
            }
            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), moduleName);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), moduleName);
            log.error("Error while Getting connection Error:{}", e.getMessage(), e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(counter.get());
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void process(Pairing pairing) {
        blackListService.save(recordMapper.getBlackListHis(pairing));
        blackListService.save(recordMapper.getBlackList(pairing));
        ImeiPairDetailHis imeiPairDetailHis = mapper.toImeiPairDetailHis(pairing);
        imeiPairDetailHis.setAction("DELETE");
        imeiPairDetailHis.setActionRemark("EXPIRED");
        imeiPairDetailHis = imeiPairDetailHisRepository.save(imeiPairDetailHis);
        String query = deletePairingQuery.replaceAll("<SYNC_STATUS>", SyncStatus.SYNCED.name()).replaceAll("<ID>", String.valueOf(pairing.getId()));
        queryExecutorService.execute(query);
        log.info("Going to Save to imei_pair_detail_his:{} ", imeiPairDetailHis);
        log.info("Saved to imei_pair_detail_his:{} ", imeiPairDetailHis);
    }
}
