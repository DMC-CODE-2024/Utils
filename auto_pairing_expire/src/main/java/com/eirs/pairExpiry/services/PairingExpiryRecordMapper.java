package com.eirs.pairExpiry.services;

import com.eirs.constants.pairing.DeviceSyncOperation;
import com.eirs.constants.pairing.GSMAStatus;
import com.eirs.constants.pairing.PairMode;
import com.eirs.pairExpiry.repository.entity.BlacklistDevice;
import com.eirs.pairExpiry.repository.entity.BlacklistDeviceHis;
import com.eirs.pairExpiry.repository.entity.Pairing;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.time.LocalDateTime;

@Service
public class PairingExpiryRecordMapper {

    public Pairing dbToPairing(ResultSet resultSet) throws Exception {
        Pairing pairing = new Pairing();
        pairing.setId(resultSet.getLong("id"));
        pairing.setImsi(resultSet.getString("imsi"));
        pairing.setActualImei(resultSet.getString("actual_imei"));
        pairing.setPairingDate(resultSet.getTimestamp("pairing_date").toLocalDateTime());
        pairing.setRecordTime(resultSet.getTimestamp("record_time").toLocalDateTime());
        pairing.setPairMode(PairMode.valueOf(resultSet.getString("pair_mode")));
        pairing.setFilename(resultSet.getString("file_name"));
        pairing.setImei(resultSet.getString("imei"));
        pairing.setGsmaStatus(GSMAStatus.valueOf(resultSet.getString("gsma_status")));
        pairing.setAllowedDays(resultSet.getInt("allowed_days"));
        pairing.setOperator(resultSet.getString("operator"));
        pairing.setMsisdn(resultSet.getString("msisdn"));
        return pairing;
    }

    public BlacklistDevice getBlackList(Pairing pairing) {
        BlacklistDevice list = new BlacklistDevice();
        list.setMsisdn(pairing.getMsisdn());
        list.setImsi(pairing.getImsi());
        list.setImei(pairing.getImei());
        list.setOperatorName(pairing.getOperator());
        list.setCreatedOn(LocalDateTime.now());
        list.setSource("PAIRING");
        return list;
    }

    public BlacklistDeviceHis getBlackListHis(Pairing pairing) {
        BlacklistDeviceHis listHis = new BlacklistDeviceHis();
        listHis.setMsisdn(pairing.getMsisdn());
        listHis.setImsi(pairing.getImsi());
        listHis.setImei(pairing.getImei());
        listHis.setOperatorName(pairing.getOperator());
        listHis.setCreatedOn(LocalDateTime.now());
        listHis.setOperation(DeviceSyncOperation.ADD.ordinal());
        listHis.setSource("PAIRING");
        return listHis;
    }
}
