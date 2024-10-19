package com.eirs.greyToBlack.services;

import com.eirs.config.AppConfig;
import com.eirs.repository.ConfigRepository;
import com.eirs.repository.entity.SysParam;
import com.eirs.repository.entity.SystemConfigKeys;
import com.eirs.services.ModuleAlertService;
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
    private Integer stolenGreyToBlackListDays;

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private AppConfig appConfig;

    @Override
    public Integer getStolenGreyToBlackListdays() {
        String key = SystemConfigKeys.stolen_grey_to_black_list_days;
        String featureName = appConfig.getFeatureName();
        try {
            if (stolenGreyToBlackListDays == null) {
                List<SysParam> values = repository.findByConfigKey(key);
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

}
