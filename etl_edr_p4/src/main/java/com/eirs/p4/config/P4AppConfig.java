package com.eirs.p4.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class P4AppConfig {

    @Value("${db-batch-size}")
    private Long batchSize;

}
