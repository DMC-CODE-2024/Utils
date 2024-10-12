package com.eirs.services;

import com.eirs.constants.NotificationLanguage;
import com.eirs.constants.SmsPlaceHolders;

import java.util.Map;

public interface SmsConfigurationService {

    String getSms(String tag, NotificationLanguage language, String moduleName);

    String getSms(String tag, Map<SmsPlaceHolders, String> smsPlaceHolder, NotificationLanguage language, String moduleName);

}
