package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.BlacklistDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;


@Repository
public interface BlacklistDeviceRepository extends JpaRepository<BlacklistDevice, Long> {

    List<BlacklistDevice> findByImei(String imei);

    List<BlacklistDevice> findByActualImei(String actualImei);
}
