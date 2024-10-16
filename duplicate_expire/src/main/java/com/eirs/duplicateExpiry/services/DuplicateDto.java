package com.eirs.duplicateExpiry.services;

import com.eirs.constants.pairing.GSMAStatus;
import com.eirs.constants.pairing.PairMode;
import com.eirs.constants.pairing.SyncStatus;
import lombok.Data;

import java.util.Date;

@Data
public class DuplicateDto {

    private Long id;

    private String filename;

    private String imei;
    private String actualImei;

    private String imsie;

    private String msisdn;

    private Date recordTime;

    private GSMAStatus gsmaStatus;

    private PairMode pairMode;

    private Date pairingDate;

    private int allowedDays;

    private String operator;

    private SyncStatus syncStatus;
}
