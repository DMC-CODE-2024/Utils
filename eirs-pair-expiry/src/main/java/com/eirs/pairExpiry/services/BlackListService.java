package com.eirs.pairExpiry.services;

import com.eirs.pairExpiry.repository.entity.BlacklistDevice;
import com.eirs.pairExpiry.repository.entity.BlacklistDeviceHis;

public interface BlackListService {

    BlacklistDevice save(BlacklistDevice blacklist);

    BlacklistDeviceHis save(BlacklistDeviceHis blacklistHis);

}
