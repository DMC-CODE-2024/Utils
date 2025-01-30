package com.eirs.pairClean.services;

import com.eirs.config.AppConfig;
import com.eirs.constants.NotificationLanguage;
import com.eirs.repository.ConfigRepository;
import com.eirs.repository.entity.SysParam;
import com.eirs.repository.entity.SystemConfigKeys;
import com.eirs.services.ModuleAlertService;
import jakarta.annotation.PostConstruct;
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
    private NotificationLanguage defaultLanguage;

    private Integer manualPairCleanUpDays;

    LocalTime pairingNotificationSmsStartTime;

    LocalTime pairingNotificationSmsEndTime;
    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private AppConfig appConfig;

    @PostConstruct
    public void init() {
        try {
            getDefaultLanguage();
            getPairingNotificationSmsStartTime();
            getManualPairCleanUpDays();
            getPairingNotificationSmsEndTime();
        } catch (Exception e) {
            Runtime.getRuntime().halt(1);
        }
    }

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
    public Integer getManualPairCleanUpDays() {
        String key = SystemConfigKeys.manual_pair_clean_up_days;
        String featureName = appConfig.getFeatureName();
        try {
            if (manualPairCleanUpDays == null) {
                List<SysParam> values = repository.findByConfigKey(key);
                if (CollectionUtils.isEmpty(values)) {
                    moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                    throw new RuntimeException("Configuration missing in sys_param for key " + key);
                } else {
                    manualPairCleanUpDays = Integer.parseInt(values.get(0).getConfigValue());
                }
            }
        } catch (Exception e) {
            log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
            throw new RuntimeException("Error for Configuration key " + key);
        }
        return manualPairCleanUpDays;
    }

    @Override
    public LocalTime getPairingNotificationSmsStartTime() {
        String key = SystemConfigKeys.pairing_notification_sms_start_time;
        List<SysParam> values = repository.findByConfigKey(key);
        if (pairingNotificationSmsStartTime == null) {
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    pairingNotificationSmsStartTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
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
        return pairingNotificationSmsStartTime;
    }

    @Override
    public LocalTime getPairingNotificationSmsEndTime() {
        String key = SystemConfigKeys.pairing_notification_sms_end_time;
        if (pairingNotificationSmsEndTime == null) {
            List<SysParam> values = repository.findByConfigKey(key);
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    pairingNotificationSmsEndTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
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
        return pairingNotificationSmsEndTime;
    }
}