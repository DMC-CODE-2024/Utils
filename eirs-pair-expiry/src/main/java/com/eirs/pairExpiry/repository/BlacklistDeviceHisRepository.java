package com.eirs.pairExpiry.repository;

import com.eirs.pairExpiry.repository.entity.BlacklistDeviceHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlacklistDeviceHisRepository extends JpaRepository<BlacklistDeviceHis, Long> {

}
