package com.eirs.duplicateExpiry.repository;

import com.eirs.duplicateExpiry.repository.entity.BlacklistDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BlacklistDeviceRepository extends JpaRepository<BlacklistDevice, Long> {

    List<BlacklistDevice> findByImei(String imei);

    List<BlacklistDevice> findByActualImei(String actualImei);
}
