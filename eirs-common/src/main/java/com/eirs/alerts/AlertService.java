package com.eirs.alerts;

import com.eirs.alerts.constants.AlertIds;
import com.eirs.alerts.constants.AlertMessagePlaceholders;

import java.util.Map;

public interface AlertService {

    void sendAlertNow(AlertIds alertIds, Map<AlertMessagePlaceholders, String> placeHolderMap);
    void sendAlert(AlertIds alertIds, Map<AlertMessagePlaceholders, String> placeHolderMap);
}
