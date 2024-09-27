package com.eirs.pairs.config;

import com.eirs.pairs.constants.DBType;
import com.eirs.pairs.constants.UtilityType;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "module-name")
public class ModuleAppConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<UtilityType, String> map;

    @PostConstruct
    public void init() {
        log.info("moduleNamesMapping Loaded from Config:{}", map);
    }

}
