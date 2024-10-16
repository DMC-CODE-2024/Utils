package com.eirs.updateDuplicateMsisdn.services;

import com.eirs.constants.NotificationLanguage;

import java.time.LocalTime;

public interface SystemConfigurationService {

    NotificationLanguage getDefaultLanguage();

    LocalTime getDuplicateNotificationSmsStartTime();

    LocalTime getDuplicateNotificationSmsEndTime();


}
