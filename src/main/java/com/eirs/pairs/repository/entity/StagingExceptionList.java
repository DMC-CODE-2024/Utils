package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "temp_exception_list")
public class StagingExceptionList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imei;

    private String imsi;

    private String msisdn;

    private String operatorId;

    private String operatorName;

    private LocalDateTime createdOn;

    private String deviceType;

    private LocalDateTime edrDatetime;

}
