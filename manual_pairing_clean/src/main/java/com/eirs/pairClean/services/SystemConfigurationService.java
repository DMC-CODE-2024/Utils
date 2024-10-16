package com.eirs.pairClean.services;

import com.eirs.constants.NotificationLanguage;

import java.time.LocalTime;

public interface SystemConfigurationService {

    NotificationLanguage getDefaultLanguage();

    Integer getManualPairCleanUpDays();

    LocalTime getPairingNotificationSmsStartTime();

    LocalTime getPairingNotificationSmsEndTime();

}
