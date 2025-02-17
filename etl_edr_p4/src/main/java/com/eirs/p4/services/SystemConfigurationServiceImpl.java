package com.eirs.p4.services;

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

import java.util.List;

@Service
public class SystemConfigurationServiceImpl implements SystemConfigurationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConfigRepository repository;
    private NotificationLanguage defaultLanguage;

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private AppConfig appConfig;

    @PostConstruct
    public void init() {
        try{
        getDefaultLanguage();
        getEdrTableCleanDays();
    }catch(Exception e) {
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
    public Integer getEdrTableCleanDays() {
        String key = SystemConfigKeys.edr_table_clean_days;
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

}
