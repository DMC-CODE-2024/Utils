package com.eirs.pairs.config;

import com.eirs.pairs.constants.DBType;
import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.constants.pairing.PairMode;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Data
@Configuration
public class AppConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${eirs.notification.url}")
    private String notificationUrl;

    @Value("${module-name}")
    private Map<UtilityType, String> moduleNamesMapping;

    @Value("${dependent.P4_PROCESS.module-name}")
    private String dependentModuleForP4;

    @PostConstruct
    public void init() {
        log.info("moduleNamesMapping Loaded from Config:{}", moduleNamesMapping);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public DBType getDbType() {
        return driverClassName.startsWith("com.mysql") ? DBType.MYSQL : driverClassName.startsWith("oracle") ? DBType.ORACLE :
                DBType.NONE;
    }


    public String getModuleName(UtilityType utilityType) {
        String processId = moduleNamesMapping.get(utilityType);
        if (processId == null) {
            log.error("Please add ModuleName Mapping for {} in application.properties", utilityType);
            throw new RuntimeException("Please add ModuleName Mapping for " + utilityType + " application.properties");
        }
        return processId;
    }
}
