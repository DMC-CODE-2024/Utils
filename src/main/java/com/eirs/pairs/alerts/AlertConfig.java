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
@ConfigurationProperties(prefix = "eirs.alert")
public class AlertConfig {

    private String url;

    private Map<AlertIds, AlertConfigDto> alertsMapping;
}
