package com.eirs.updateDuplicateMsisdn.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.NotificationLanguage;
import com.eirs.repository.ConfigRepository;
import com.eirs.repository.entity.SysParam;
import com.eirs.repository.entity.SystemConfigKeys;
import com.eirs.services.ModuleAlertService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.List;

@Service
public class SystemConfigurationServiceImpl implements SystemConfigurationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConfigRepository repository;
    LocalTime duplicateNotificationSmsStartTime;

    LocalTime duplicateNotificationSmsEndTime;

    Boolean sendDuplicationNotification;
    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private AppConfig appConfig;

    private NotificationLanguage defaultLanguage;

    @Override
    public NotificationLanguage getDefaultLanguage() {
        if (defaultLanguage == null) {
            List<SysParam> values = repository.findByConfigKey(SystemConfigKeys.default_language);
            if (CollectionUtils.isEmpty(values)) {
                defaultLanguage = NotificationLanguage.en;
            } else {
                defaultLanguage = NotificationLanguage.valueOf(values.get(0).getConfigValue());
            }
        }
        return defaultLanguage;
    }

    @Override
    public LocalTime getDuplicateNotificationSmsStartTime() {
        String key = SystemConfigKeys.duplicate_notification_sms_start_time;
        List<SysParam> values = repository.findByConfigKey(key);
        if (duplicateNotificationSmsStartTime == null) {
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    duplicateNotificationSmsStartTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, value, appConfig.getFeatureName());
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getFeatureName(), e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getFeatureName());
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return duplicateNotificationSmsStartTime;
    }

    @Override
    public LocalTime getDuplicateNotificationSmsEndTime() {
        String key = SystemConfigKeys.duplicate_notification_sms_end_time;
        if (duplicateNotificationSmsEndTime == null) {
            List<SysParam> values = repository.findByConfigKey(key);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    duplicateNotificationSmsEndTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getFeatureName());
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getFeatureName(), e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getFeatureName());
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return duplicateNotificationSmsEndTime;
    }

    @Override
    public Boolean sendDuplicationNotificationFlag() {
        String key = SystemConfigKeys.send_duplication_notification_flag;
        if (sendDuplicationNotification == null) {
            List<SysParam> values = repository.findByConfigKey(key);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                if (StringUtils.equalsAnyIgnoreCase(value, "YES", "TRUE"))
                    sendDuplicationNotification = Boolean.TRUE;
                else
                    sendDuplicationNotification = Boolean.FALSE;
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getFeatureName());
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getFeatureName());
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return sendDuplicationNotification;
    }
}
