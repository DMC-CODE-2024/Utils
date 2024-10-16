package com.eirs.pairExpiry.services;

import com.eirs.pairExpiry.repository.BlacklistDeviceHisRepository;
import com.eirs.pairExpiry.repository.BlacklistDeviceRepository;
import com.eirs.pairExpiry.repository.entity.BlacklistDevice;
import com.eirs.pairExpiry.repository.entity.BlacklistDeviceHis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlackListServiceImpl implements BlackListService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BlacklistDeviceHisRepository blacklistHisRepository;

    @Autowired
    BlacklistDeviceRepository blacklistRepository;

    public BlacklistDevice save(BlacklistDevice blacklist) {
        blacklist = blacklistRepository.save(blacklist);
        log.info("Saved in to Blacklist:{}", blacklist);
        return blacklist;
    }

    public BlacklistDeviceHis save(BlacklistDeviceHis blacklistHis) {
        blacklistHis = blacklistHisRepository.save(blacklistHis);
        log.info("Saved in to BlacklistHis:{}", blacklistHis);
        return blacklistHis;
    }
}
