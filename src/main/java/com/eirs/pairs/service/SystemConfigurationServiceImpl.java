package com.eirs.pairs.service;

import com.eirs.pairs.alerts.AlertConfig;
import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.reminder.GenericReminderNotificationProcess;
import com.eirs.pairs.repository.ConfigRepository;
import com.eirs.pairs.repository.entity.SysParam;
import com.eirs.pairs.repository.entity.SystemConfigKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SystemConfigurationServiceImpl implements SystemConfigurationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConfigRepository repository;
    LocalTime notificationSmsStartTime;

    LocalTime notificationSmsEndTime;

    private NotificationLanguage defaultLanguage;

    private Integer manualPairCleanUpDays;

    private Integer mgmtInitStartCleanUpHours;

    private Integer stolenGreyToBlackListDays;

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
    public Integer getManualPairCleanUpDays() {
        String key = SystemConfigKeys.manual_pair_clean_up_days;
        String featureName = appConfig.getModuleName(UtilityType.PAIRING_CLEAN);
        try {
            if (manualPairCleanUpDays == null) {
                List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
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
    public Integer getMgmtInitStartCleanUpHours() {
        String key = SystemConfigKeys.mgmt_init_start_clean_up_hours;
        String featureName = appConfig.getModuleName(UtilityType.PAIR_MGMT_INIT_START_CLEAN);
        try {
            if (mgmtInitStartCleanUpHours == null) {
                List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
                if (CollectionUtils.isEmpty(values)) {
                    moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                    throw new RuntimeException("Configuration missing in sys_param for key " + key);
                } else {
                    mgmtInitStartCleanUpHours = Integer.parseInt(values.get(0).getConfigValue());
                }
            }
        } catch (Exception e) {
            log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
            throw new RuntimeException("Error for Configuration key " + key);
        }
        return mgmtInitStartCleanUpHours;
    }

    @Override
    public Integer getStolenGreyToBlackListdays() {
        String key = SystemConfigKeys.stolen_grey_to_black_list_days;
        String featureName = appConfig.getModuleName(UtilityType.GREY_TO_BLACKLIST);
        try {
            if (stolenGreyToBlackListDays == null) {
                List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
                if (CollectionUtils.isEmpty(values)) {
                    moduleAlertService.sendConfigurationMissingAlert(key, featureName);
                    throw new RuntimeException("Configuration missing in sys_param for key " + key);
                } else {
                    stolenGreyToBlackListDays = Integer.parseInt(values.get(0).getConfigValue());
                }
            }
        } catch (Exception e) {
            log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, featureName, e.getMessage());
            throw new RuntimeException("Error for Configuration key " + key);
        }
        return stolenGreyToBlackListDays;
    }

    @Override
    public Integer getGenericReminderFirstNotificationDays() {
        String key = GenericReminderNotificationProcess.processName + "_" + SystemConfigKeys.generic_reminder_first_notification_days;
        String featureName = appConfig.getModuleName(UtilityType.REMINDER_UTILITY);
        try {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
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
        String featureName = appConfig.getModuleName(UtilityType.REMINDER_UTILITY);
        try {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
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
        String featureName = appConfig.getModuleName(UtilityType.REMINDER_UTILITY);
        try {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
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
        String featureName = appConfig.getModuleName(UtilityType.REMINDER_UTILITY);
        try {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
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
        String featureName = appConfig.getModuleName(UtilityType.REMINDER_UTILITY);
        try {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
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
    public Integer getEdrTableCleanDays() {
        String key = SystemConfigKeys.edr_table_clean_days;
        String featureName = appConfig.getModuleName(UtilityType.P4_PROCESS);
        try {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, featureName);
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
    public LocalTime getNotificationSmsStartTime(String moduleName) {
        String key = SystemConfigKeys.notification_sms_start_time;
        List<SysParam> values = repository.findByConfigKeyAndModule(key, moduleName);
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
        String key = SystemConfigKeys.notification_sms_end_time;
        if (notificationSmsEndTime == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, moduleName);
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
