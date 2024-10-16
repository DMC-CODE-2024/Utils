package com.eirs.p4.services;

import com.eirs.constants.NotificationLanguage;

public interface SystemConfigurationService {

    NotificationLanguage getDefaultLanguage();

    Integer getEdrTableCleanDays();

}
