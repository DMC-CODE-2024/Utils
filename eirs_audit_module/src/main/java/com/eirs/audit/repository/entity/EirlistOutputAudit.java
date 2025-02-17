package com.eirs.audit.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "eirlist_output_aud", catalog = "aud")
public class EirlistOutputAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actualImei;

    private String imei;

    private String imsi;

    private String msisdn;

    private String tac;

    private String operator;

    private java.util.Date createdOn;

    private java.util.Date modifiedOn;

    private String missingSource;

    private String fileName;

    private String listName;

    private Date blockedDate;

    private Integer eirNo;
}
