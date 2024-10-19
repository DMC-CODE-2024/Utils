package com.eirs.services;

import com.eirs.constants.NotificationLanguage;
import com.eirs.constants.SmsPlaceHolders;
import com.eirs.repository.SmsConfigurationEntityRepository;
import com.eirs.repository.entity.SmsConfigurationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsConfigurationServiceImpl implements SmsConfigurationService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SmsConfigurationEntityRepository smsConfigurationEntityRepository;

    @Autowired
    ModuleAlertService moduleAlertService;

    @Override
    public String getSms(String tag, NotificationLanguage language, String moduleName) {
        String retVal = null;
        try {
            SmsConfigurationEntity smsConfiguration = smsConfigurationEntityRepository.findByTagAndLanguage(tag, language);
            if (smsConfiguration == null) {
                moduleAlertService.sendSmsConfigMissingAlert(tag, moduleName, language.name());
                retVal = "";
                log.info("Default SMS[{}] as SMS not found from eirs_response_param table for tag:{} language:{}", retVal, tag, language);
            } else {
                retVal = smsConfiguration.getMsg();
                log.info("SMS[{}] found from eirs_response_param table for tag:{} language:{}", retVal, tag, language);
            }
        } catch (Exception e) {
            retVal = "";
            log.error("Default SMS[{}] as Error while getting from eirs_response_param table for tag:{} language:{} Error:{}", retVal, tag, language, e.getMessage());
        }
        return retVal;
    }

    @Override
    public String getSms(String tag, Map<SmsPlaceHolders, String> smsPlaceHolder, NotificationLanguage language, String moduleName) {
        String sms = getSms(tag, language, moduleName);
        return getMsg(smsPlaceHolder, sms);
    }

    private String getMsg(Map<SmsPlaceHolders, String> smsPlaceHolder, String msg) {
        String finalMsg = msg;
        if (smsPlaceHolder != null) for (SmsPlaceHolders key : smsPlaceHolder.keySet())
            finalMsg = finalMsg.replaceAll(key.getPlaceHolder(), smsPlaceHolder.get(key));
        return finalMsg;
    }
}
