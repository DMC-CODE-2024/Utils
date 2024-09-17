package com.eirs.pairs.service;

import com.eirs.pairs.duplicateToBlack.DuplicateDto;
import com.eirs.pairs.repository.entity.BlacklistDevice;
import com.eirs.pairs.repository.entity.BlacklistDeviceHis;

public interface BlackListService {

    BlacklistDevice save(BlacklistDevice blacklist);

    BlacklistDeviceHis save(BlacklistDeviceHis blacklistHis);

    public void add(DuplicateDto fileDataDto);

    void addAndUpdate(DuplicateDto fileDataDto);
}
