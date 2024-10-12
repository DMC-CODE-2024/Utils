package com.eirs.duplicateExpiry.services;

import com.eirs.constants.ExceptionListConstants;
import com.eirs.constants.pairing.DeviceSyncOperation;
import com.eirs.duplicateExpiry.repository.ExceptionListHisRepository;
import com.eirs.duplicateExpiry.repository.ExceptionListRepository;
import com.eirs.duplicateExpiry.repository.entity.ExceptionList;
import com.eirs.duplicateExpiry.repository.entity.ExceptionListHis;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExceptionListServiceImpl implements ExceptionListService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExceptionListRepository exceptionListRepository;

    @Autowired
    ExceptionListHisRepository exceptionListHisRepository;

    @Override
    public ExceptionList save(ExceptionList exceptionList) {
        log.info("Going to save in Exception List : {}", exceptionList);
        exceptionList = exceptionListRepository.save(exceptionList);
        log.info("Saved in to ExceptionList:{}", exceptionList);
        return exceptionList;
    }

    @Override
    public ExceptionListHis save(ExceptionListHis exceptionListHis) {
        log.info("Going to save in Exception List Hist: {}", exceptionListHis);
        exceptionListHis = exceptionListHisRepository.save(exceptionListHis);
        log.info("Saved in to ExceptionListHis:{}", exceptionListHis);
        return exceptionListHis;
    }

    @Override
    public ExceptionList getNotVIPImeiAndImsi(String imei, String imsi) {
        log.info("Going to find for Not VIP in exception list using imei:{} imsi : {}", imei, imsi);
        List<ExceptionList> exceptionLists = exceptionListRepository.findByImeiAndImsi(imei, imsi);
        for (ExceptionList e : exceptionLists) {
            if (!StringUtils.equalsAnyIgnoreCase(e.getRequestType(), ExceptionListConstants.VIP.name())) {
                return e;
            }
        }
        log.info("Non VIP in exception list using imei:{}, imsi : {}, is : {}", imei, imsi, exceptionLists);
        return null;
    }

    @Override
    public List<ExceptionList> getVIPImsi(String imsi) {
        log.info("Going to find for VIP in exception list using imsi : {}", imsi);
        List<ExceptionList> exceptionLists = exceptionListRepository.findByImsiAndRequestType(imsi, ExceptionListConstants.VIP.name());
        log.info("VIP in exception list using imsi : {}, is : {}", imsi, exceptionLists);
        return exceptionLists;
    }

    @Override
    public List<ExceptionList> getByImeiAndImsi(String imei, String imsi) {
        log.info("Going to check exception list using imei : {} imsi : {}", imei, imsi);
        List<ExceptionList> exceptionLists = exceptionListRepository.findByImeiAndImsi(imei, imsi);
        log.info("exception list using imei : {} imsi : {}, is : {}", imei, imsi, exceptionLists);
        return exceptionLists;
    }


    @Override
    public void add(DuplicateDto fileDataDto) {
        ExceptionList exceptionList = new ExceptionList();
        exceptionList.setImei(fileDataDto.getImei().substring(0, 14));
        exceptionList.setActualImei(fileDataDto.getImei());
        exceptionList.setImsi(fileDataDto.getImsie());
        exceptionList.setCreatedOn(LocalDateTime.now());
        exceptionList.setMsisdn(fileDataDto.getMsisdn());
        exceptionList.setOperatorId(null);
        exceptionList.setSource("DUPLICATE");
        exceptionList.setTac(fileDataDto.getImei().substring(0, 8));
        exceptionList.setOperatorName(fileDataDto.getOperator());

        ExceptionListHis exceptionListHis = new ExceptionListHis();
        exceptionListHis.setOperation(DeviceSyncOperation.ADD.ordinal());
        exceptionListHis.setImei(fileDataDto.getImei().substring(0, 14));
        exceptionListHis.setActualImei(fileDataDto.getImei());
        exceptionListHis.setImsi(fileDataDto.getImsie());
        exceptionListHis.setCreatedOn(LocalDateTime.now());
        exceptionListHis.setMsisdn(fileDataDto.getMsisdn());
        exceptionListHis.setOperatorId(null);
        exceptionListHis.setTac(fileDataDto.getImei().substring(0, 8));
        exceptionListHis.setSource("DUPLICATE");
        exceptionListHis.setOperatorName(fileDataDto.getOperator());

        exceptionListHis = exceptionListHisRepository.save(exceptionListHis);
        log.info("Added to ExceptionListHis:{} fileDataDto:{}", exceptionListHis, fileDataDto);
        exceptionList = exceptionListRepository.save(exceptionList);
        log.info("Added to ExceptionList:{} fileDataDto:{}", exceptionList, fileDataDto);
    }

    @Override
    public void delete(DuplicateDto fileDataDto) {
        log.info("Deleting fileDataDto:{} from Exception List", fileDataDto);
        List<ExceptionList> exceptionLists = exceptionListRepository.findByImeiAndImsi(fileDataDto.getImei(), fileDataDto.getImsie());
        exceptionListRepository.deleteAll(exceptionLists);
        log.info("Deleted fileDataDto:{} from Exception List {}", fileDataDto, exceptionLists);
        if (CollectionUtils.isEmpty(exceptionLists)) {
            return;
        }
        ExceptionList exceptionList = exceptionLists.get(0);
        ExceptionListHis exceptionListHis = new ExceptionListHis();
        exceptionListHis.setOperatorName(exceptionList.getOperatorName());
        exceptionListHis.setImsi(exceptionList.getImsi());
        exceptionListHis.setMsisdn(exceptionList.getMsisdn());
        exceptionListHis.setCreatedOn(LocalDateTime.now());
        exceptionListHis.setOperation(DeviceSyncOperation.DELETE.ordinal());
        exceptionListHis.setImei(exceptionList.getImei());
        exceptionListHis.setActualImei(exceptionList.getActualImei());
        exceptionListHis.setTxnId(exceptionList.getTxnId());
        exceptionListHis.setSource("DUPLICATE");
        exceptionListHis = exceptionListHisRepository.save(exceptionListHis);
        log.info("Added in Exception History exceptionListHis:{} ", exceptionListHis);
    }
}
