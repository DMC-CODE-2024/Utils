package com.eirs.reminder.services;

import com.eirs.constants.NotificationLanguage;

import java.time.LocalTime;

public interface SystemConfigurationService {

    NotificationLanguage getDefaultLanguage();
    Integer getGenericReminderFirstNotificationDays();

    Integer getGenericReminderSecondNotificationDays();

    Integer getGenericReminderThirdNotificationDays();

    String getGenericReminderTableName();

    String getGenericReminderWhereClause();

    LocalTime getNotificationSmsStartTime(String moduleName);

    LocalTime getNotificationSmsEndTime(String moduleName);

}
