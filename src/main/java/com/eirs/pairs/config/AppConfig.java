package com.eirs.pairs.config;

import com.eirs.pairs.constants.DBType;
import com.eirs.pairs.constants.pairing.PairMode;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
public class AppConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${notification.url}")
    private String notificationUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public DBType getDbType() {
        return driverClassName.startsWith("com.mysql") ? DBType.MYSQL : driverClassName.startsWith("oracle") ? DBType.ORACLE :
                DBType.NONE;
    }
}
