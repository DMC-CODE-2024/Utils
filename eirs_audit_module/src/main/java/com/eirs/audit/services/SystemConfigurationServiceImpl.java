package com.eirs.audit.services;

import com.eirs.config.AppConfig;
import com.eirs.audit.constant.AuditSystemConfigKeys;
import com.eirs.repository.ConfigRepository;
import com.eirs.repository.entity.SysParam;
import com.eirs.services.ModuleAlertService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class SystemConfigurationServiceImpl implements SystemConfigurationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConfigRepository repository;
    List<String> operators = new ArrayList<>();

    Map<String, Integer> operatorsEirs = new HashMap<>();

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private AppConfig appConfig;

    Map<String, String> shortCodeWithOperatorMap = new HashMap<>();

    @PostConstruct
    public void init() {
        getOperators();
    }

    @Override
    public synchronized List<String> getOperators() {
        if (CollectionUtils.isEmpty(operators)) {
            Integer noOfOperators = findByKey(AuditSystemConfigKeys.NO_OF_OPERATORS, 0);
            for (int i = 1; i <= noOfOperators; i++) {
                String operator = findByKey(AuditSystemConfigKeys.OPERATOR.replaceAll("<NUMBER>", String.valueOf(i)));
                Integer noOfEirs = findByKey(AuditSystemConfigKeys.OPERATOR_NO_OF_EIRS.replaceAll("<OPERATOR>", operator), 1);
                operators.add(operator.toUpperCase());
                log.info("NoOfEirs:{} for Operator:{}", noOfEirs, operator);
                String shortCode = findByKey(AuditSystemConfigKeys.SHORT_CODE.replaceAll("<OPERATOR>", operator));
                operatorsEirs.put(operator.toUpperCase(), noOfEirs);
                shortCodeWithOperatorMap.put(operator, shortCode);
            }
        }
        return operators;
    }

    @Override
    public String getShortCode(String operator) {
        return shortCodeWithOperatorMap.get(operator);
    }

    @Override
    public Integer getNoOfEirs(String operator) {
        Integer value = operatorsEirs.get(operator.toUpperCase());
        if (value == null) {
            log.error("Please check No of Eirs not configured for operator:{}", operator);
            return 0;
        }
        return value;
    }

    public String findByKey(String key) throws RuntimeException {
        try {
            Optional<SysParam> optional = repository.findByConfigKeyIgnoreCase(key);
            if (optional.isPresent()) {
                log.info("Filled key:{} value:{} FeatureName:{}", key, optional.get().getConfigValue(), appConfig.getFeatureName());
                return optional.get().getConfigValue();
            } else {
                log.info("Value for key:{} Not Found for FeatureName:{}", key, appConfig.getFeatureName());
                moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getFeatureName());
                throw new RuntimeException("Config Key:" + key + ", value not found for FeatureName:" + appConfig.getFeatureName());
            }
        } catch (Exception e) {
            log.error("Error while finding Key:{} FeatureName:{} Error:{}", key, appConfig.getFeatureName(), e.getMessage(), e);
            throw new RuntimeException("Config Key:" + key + ", value not found for FeatureName:" + appConfig.getFeatureName());
        }
    }


    private Integer findByKey(String key, int defaultValue) {
        String value = null;
        try {
            value = findByKey(key);
            try {
                return Integer.parseInt(value);
            } catch (RuntimeException e) {
                moduleAlertService.sendConfigurationWrongValueAlert(key, value, appConfig.getFeatureName());
                return defaultValue;
            }
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }


}
