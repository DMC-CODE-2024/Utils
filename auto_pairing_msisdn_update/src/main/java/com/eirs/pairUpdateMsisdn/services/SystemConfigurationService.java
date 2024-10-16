package com.eirs.pairUpdateMsisdn.services;

import com.eirs.constants.NotificationLanguage;

import java.time.LocalTime;

public interface SystemConfigurationService {

    NotificationLanguage getDefaultLanguage();

    LocalTime getPairingNotificationSmsStartTime();

    LocalTime getPairingNotificationSmsEndTime();

}
