package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.BlacklistDeviceHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlacklistDeviceHisRepository extends JpaRepository<BlacklistDeviceHis, Long> {

}
