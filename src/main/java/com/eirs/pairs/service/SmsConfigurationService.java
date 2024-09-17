package com.eirs.pairs.service;

import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.constants.SmsPlaceHolders;
import com.eirs.pairs.constants.SmsTag;

import java.util.Map;

public interface SmsConfigurationService {

    String getSms(String tag, NotificationLanguage language, String moduleName);

    String getSms(String tag, Map<SmsPlaceHolders, String> smsPlaceHolder, NotificationLanguage language, String moduleName);

}
