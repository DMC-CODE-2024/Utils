package com.eirs.alerts;

import com.eirs.alerts.constants.AlertMessagePlaceholders;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {

    private String alertId;

    private String alertProcess;

    private String featureName;

    private String priority;

    private String txnid;

    private Map<AlertMessagePlaceholders, String> placeHolderMap;
}
