package com.eirs.pairs.alerts;

import com.eirs.pairs.alerts.constants.AlertIds;
import com.eirs.pairs.constants.UtilityType;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Data
@ConfigurationProperties(prefix = "alerts")
public class AlertConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String postUrl;

    private String processId;

    private Map<AlertIds, AlertConfigDto> alertsMapping;

    private Map<UtilityType, String> processMapping;

    @PostConstruct
    public void init() {
        log.info("ProcessMapping Loaded from Config:{}", processMapping);
    }

    public String getProcessId(UtilityType utilityType) {
        String processId = processMapping.get(utilityType);
        if (processId == null) {
            log.error("Please add ProcessId for {} in application.properties", utilityType);
            throw new RuntimeException("Please add ProcessId for " + utilityType + " application.properties");
        }
        return processId;
    }
}
