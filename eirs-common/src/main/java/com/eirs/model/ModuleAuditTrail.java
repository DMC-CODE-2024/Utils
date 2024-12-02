package com.eirs.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAuditTrail {

    private LocalDateTime createdOn;
    private Date createdDate;
    private Integer statusCode;
    private String featureName;
    private String moduleName;
    private Integer count;
    private Long timeTaken;
}
