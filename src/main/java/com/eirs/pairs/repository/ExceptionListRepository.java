package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.ExceptionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExceptionListRepository extends JpaRepository<ExceptionList, Long> {

    List<ExceptionList> findByImeiAndImsi(String imei, String imsi);

    List<ExceptionList> findByActualImei(String actualImei);

    List<ExceptionList> findByImsiAndRequestType(String imsi, String requestType);

    ExceptionList findByImeiAndImsiAndRequestType(String imei, String imsi, String requestType);
}
