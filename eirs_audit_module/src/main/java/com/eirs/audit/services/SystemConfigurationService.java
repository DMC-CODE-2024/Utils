package com.eirs.audit.services;

import com.eirs.constants.NotificationLanguage;

import java.time.LocalTime;
import java.util.List;

public interface SystemConfigurationService {

    List<String> getOperators();

    Integer getNoOfEirs(String operator);

    String getShortCode(String operator);

    String findByKey(String key) throws RuntimeException;

}
