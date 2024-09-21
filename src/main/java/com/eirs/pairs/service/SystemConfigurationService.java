package com.eirs.pairs.service;

import com.eirs.pairs.constants.NotificationLanguage;

import java.time.LocalTime;
import java.util.Set;

public interface SystemConfigurationService {

    NotificationLanguage getDefaultLanguage();

    Integer getManualPairCleanUpDays();

    Integer getMgmtInitStartCleanUpHours();

    Integer getStolenGreyToBlackListdays();

    Integer getGenericReminderFirstNotificationDays();

    Integer getGenericReminderSecondNotificationDays();

    Integer getGenericReminderThirdNotificationDays();

    String getGenericReminderTableName();

    String getGenericReminderWhereClause();

    Integer getEdrTableCleanDays();

    LocalTime getNotificationSmsStartTime(String moduleName);

    LocalTime getNotificationSmsEndTime(String moduleName);

}
