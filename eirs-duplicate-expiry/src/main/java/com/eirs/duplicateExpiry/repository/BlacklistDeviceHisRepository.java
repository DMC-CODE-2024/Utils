package com.eirs.duplicateExpiry.repository;

import com.eirs.duplicateExpiry.repository.entity.BlacklistDeviceHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlacklistDeviceHisRepository extends JpaRepository<BlacklistDeviceHis, Long> {

}
