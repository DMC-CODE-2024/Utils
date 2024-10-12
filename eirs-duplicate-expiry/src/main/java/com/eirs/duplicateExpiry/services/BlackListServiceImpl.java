package com.eirs.duplicateExpiry.services;

import com.eirs.constants.pairing.DeviceSyncOperation;
import com.eirs.duplicateExpiry.repository.BlacklistDeviceHisRepository;
import com.eirs.duplicateExpiry.repository.BlacklistDeviceRepository;
import com.eirs.duplicateExpiry.repository.entity.BlacklistDevice;
import com.eirs.duplicateExpiry.repository.entity.BlacklistDeviceHis;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlackListServiceImpl implements BlackListService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BlacklistDeviceHisRepository blacklistHisRepository;

    @Autowired
    BlacklistDeviceRepository blacklistRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public BlacklistDevice save(BlacklistDevice blacklist) {
        blacklist = blacklistRepository.save(blacklist);
        log.info("Saved in to Blacklist:{}", blacklist);
        return blacklist;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public BlacklistDeviceHis save(BlacklistDeviceHis blacklistHis) {
        blacklistHis = blacklistHisRepository.save(blacklistHis);
        log.info("Saved in to BlacklistHis:{}", blacklistHis);
        return blacklistHis;
    }


    @Override
    public void add(DuplicateDto fileDataDto) {
        BlacklistDeviceHis blacklistHis = new BlacklistDeviceHis();
        blacklistHis.setOperation(DeviceSyncOperation.ADD.ordinal());
        blacklistHis.setImei(fileDataDto.getImei().substring(0, 14));
        blacklistHis.setActualImei(fileDataDto.getImei());
        blacklistHis.setImsi(fileDataDto.getImsie());
        blacklistHis.setCreatedOn(LocalDateTime.now());
        blacklistHis.setMsisdn(fileDataDto.getMsisdn());
        blacklistHis.setOperatorId(null);
        blacklistHis.setOperatorName(fileDataDto.getOperator());
        blacklistHis.setSource("DUPLICATE");
        blacklistHis.setTac(fileDataDto.getImei().substring(0, 8));

        BlacklistDevice blacklist = new BlacklistDevice();
        blacklist.setImei(fileDataDto.getImei().substring(0, 14));
        blacklist.setActualImei(fileDataDto.getImei());
        blacklist.setImsi(fileDataDto.getImsie());
        blacklist.setCreatedOn(LocalDateTime.now());
        blacklist.setMsisdn(fileDataDto.getMsisdn());
        blacklist.setOperatorId(null);
        blacklist.setOperatorName(fileDataDto.getOperator());
        blacklist.setSource("DUPLICATE");
        blacklist.setTac(fileDataDto.getImei().substring(0, 8));
//      blacklist.setOperatorName(fileDataDto.getOperatorName());

        blacklistHis = blacklistHisRepository.save(blacklistHis);
        log.info("Added to BlackListHis:{} fileDataDto:{}", blacklistHis, fileDataDto);
        blacklist = blacklistRepository.save(blacklist);
        log.info("Added to BlackList:{} fileDataDto:{}", blacklist, fileDataDto);
    }

    @Override
    public void addAndUpdate(DuplicateDto recordDataDto) {
        List<BlacklistDevice> blacklists = blacklistRepository.findByImei(recordDataDto.getImei());
        log.info("Checked for Blacklist recordDataDto:{} blacklists:{}", recordDataDto, blacklists);
        if (CollectionUtils.isEmpty(blacklists)) {
            add(recordDataDto);
        } else {
            for (BlacklistDevice blacklist : blacklists) {
                if (StringUtils.isBlank(blacklist.getSource())) {
                    blacklist.setSource("DUPLICATE");
                } else {
                    if (blacklist.getSource().contains("DUPLICATE")) {
                        log.error("Blacklist already exist with Source:DUPLICATE for recordDataDto:{}", recordDataDto);
                    } else {
                        blacklist.setSource(blacklist.getSource() + "," + "DUPLICATE");
                    }
                }
            }
            log.info("Going to update blacklists:{}", blacklists);
            blacklistRepository.saveAll(blacklists);
            log.info("Updated for Source for blacklists:{}", blacklists);
        }
    }
}
