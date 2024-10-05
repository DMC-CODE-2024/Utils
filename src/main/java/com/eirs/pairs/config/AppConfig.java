package com.eirs.pairs.config;

import com.eirs.pairs.constants.DBType;
import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.constants.pairing.PairMode;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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

    @Autowired
    private ModuleAppConfig moduleAppConfig;

    @Value("${dependent.P4_PROCESS.module-name}")
    private String dependentModuleForP4;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(60_000);
        clientHttpRequestFactory.setReadTimeout(60_000);
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        return restTemplate;
    }

    public DBType getDbType() {
        return driverClassName.startsWith("com.mysql") ? DBType.MYSQL : driverClassName.startsWith("oracle") ? DBType.ORACLE :
                DBType.NONE;
    }


    public String getModuleName(UtilityType utilityType) {
        String processId = moduleAppConfig.getMap().get(utilityType);
        if (processId == null) {
            log.error("Please add ModuleName Mapping for {} in application.properties", utilityType);
            throw new RuntimeException("Please add ModuleName Mapping for " + utilityType + " application.properties");
        }
        return processId;
    }
}
