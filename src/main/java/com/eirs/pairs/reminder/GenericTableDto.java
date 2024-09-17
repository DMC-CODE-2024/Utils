package com.eirs.pairs.reminder;

import lombok.Data;

import java.util.Date;

@Data
public class GenericTableDto {

    private Long id;

    private String actualImei;

    private String transactionId;

    private String imei;

    private String msisdn;

    private String imsie;

    private Integer reminderStatus;

    private Date createdOn;
}
