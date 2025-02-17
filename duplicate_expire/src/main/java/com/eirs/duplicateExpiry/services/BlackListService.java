package com.eirs.duplicateExpiry.services;

import com.eirs.duplicateExpiry.repository.entity.BlacklistDevice;
import com.eirs.duplicateExpiry.repository.entity.BlacklistDeviceHis;

public interface BlackListService {

    BlacklistDevice save(BlacklistDevice blacklist);

    BlacklistDeviceHis save(BlacklistDeviceHis blacklistHis);

    public void add(DuplicateDto fileDataDto);

    void addAndUpdate(DuplicateDto fileDataDto);
}
