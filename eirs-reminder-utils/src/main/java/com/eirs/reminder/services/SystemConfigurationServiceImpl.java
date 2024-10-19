package com.eirs.reminder.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.NotificationLanguage;
import com.eirs.repository.ConfigRepository;
import com.eirs.repository.entity.SysParam;
import com.eirs.repository.entity.SystemConfigKeys;
import com.eirs.services.ModuleAlertService;
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
    LocalTime notificationSmsStartTime;

    LocalTime notificationSmsEndTime;

    private NotificationLanguage defaultLanguage;

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private AppConfig appConfig;

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
    public Integer getGenericReminderFirstNotificationDays() {
        String key = GenericReminderNotificationProcess.processName + "_" + SystemConfigKeys.generic_reminder_first_notification_days;
        String featureName = appConfig.getFeatureName();
        try {
            List<SysParam> values =repository.findByConfigKey(key);
            if (CollectionUtils.isEmpty(values)) {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            } else {
                return Integer.parseInt(values.get(0).getConfigValue());
            }
        } catch (Exception e) {
            log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
            throw new RuntimeException("Error for Configuration key " + key);
        }
    }

    @Override
    public Integer getGenericReminderSecondNotificationDays() {
        String key = GenericReminderNotificationProcess.processName + "_" + SystemConfigKeys.generic_reminder_second_notification_days;
        String featureName = appConfig.getFeatureName();
        try {
            List<SysParam> values = repository.findByConfigKey(key);
            if (CollectionUtils.isEmpty(values)) {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            } else {
                return Integer.parseInt(values.get(0).getConfigValue());
            }
        } catch (Exception e) {
            log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
            throw new RuntimeException("Error for Configuration key " + key);
        }
    }

    @Override
    public Integer getGenericReminderThirdNotificationDays() {
        String key = GenericReminderNotificationProcess.processName + "_" + SystemConfigKeys.generic_reminder_third_notification_days;
        String featureName = appConfig.getFeatureName();
        try {
            List<SysParam> values = repository.findByConfigKey(key);
            if (CollectionUtils.isEmpty(values)) {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            } else {
                return Integer.parseInt(values.get(0).getConfigValue());
            }
        } catch (Exception e) {
            log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
            throw new RuntimeException("Error for Configuration key " + key);
        }
    }

    @Override
    public String getGenericReminderTableName() {
        String key = GenericReminderNotificationProcess.processName + "_" + SystemConfigKeys.generic_reminder_table_name;
        String featureName = appConfig.getFeatureName();
        try {
            List<SysParam> values = repository.findByConfigKey(key);
            if (CollectionUtils.isEmpty(values)) {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            } else {
                return values.get(0).getConfigValue();
            }
        } catch (Exception e) {
            log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
            throw new RuntimeException("Error for Configuration key " + key);
        }
    }

    @Override
    public String getGenericReminderWhereClause() {
        String key = GenericReminderNotificationProcess.processName + "_" + SystemConfigKeys.generic_reminder_where_clause;
        String featureName = appConfig.getFeatureName();
        try {
            List<SysParam> values = repository.findByConfigKey(key);
            if (CollectionUtils.isEmpty(values)) {
                moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            } else {
                return values.get(0).getConfigValue();
            }
        } catch (Exception e) {
            log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
            throw new RuntimeException("Error for Configuration key " + key);
        }
    }

    @Override
    public LocalTime getNotificationSmsStartTime(String moduleName) {
        String key = GenericReminderNotificationProcess.processName + "_" + SystemConfigKeys.notification_sms_start_time;
        List<SysParam> values =repository.findByConfigKey(key);
        if (notificationSmsStartTime == null) {
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    notificationSmsStartTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, value, moduleName);
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, moduleName, e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, moduleName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return notificationSmsStartTime;
    }

    @Override
    public LocalTime getNotificationSmsEndTime(String moduleName) {
        String key = GenericReminderNotificationProcess.processName + "_" + SystemConfigKeys.notification_sms_end_time;
        if (notificationSmsEndTime == null) {
            List<SysParam> values = repository.findByConfigKey(key);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    notificationSmsEndTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationMissingAlert(key, moduleName);
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, moduleName, e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, moduleName);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return notificationSmsEndTime;
    }

}
