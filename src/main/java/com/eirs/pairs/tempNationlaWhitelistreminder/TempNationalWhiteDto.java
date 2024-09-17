package com.eirs.pairs.tempNationlaWhitelistreminder;

import lombok.Data;

import java.util.Date;

@Data
public class TempNationalWhiteDto {

    private Long nationalWhitelistId;

    private String actualImei;

    private String imei;

    private String msisdn;

    private String imsie;

    private Integer reminderStatus;

    private String mobileOperator;

    private String actualOperator;

    private Date createdOn;
}
