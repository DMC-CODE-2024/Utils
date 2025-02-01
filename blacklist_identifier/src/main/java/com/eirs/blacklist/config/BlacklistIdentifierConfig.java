package com.eirs.blacklist.config;

import com.eirs.constants.DBType;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
public class BlacklistIdentifierConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${imei-null-pattern:}")
    private String imeiNullPattern;

}
