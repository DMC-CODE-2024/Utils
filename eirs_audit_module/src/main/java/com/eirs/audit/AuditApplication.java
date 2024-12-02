package com.eirs.audit;

import com.eirs.alerts.AlertServiceImpl;
import com.eirs.audit.orchestrator.AuditOrchestrator;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableEncryptableProperties
@Slf4j
@EnableJpaRepositories({"com.eirs.audit", "com.eirs.repository"})
@EntityScan({"com.eirs.audit", "com.eirs.repository"})
@ComponentScan({"com.eirs"})
public class AuditApplication implements CommandLineRunner {

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AuditApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            LocalDate date = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            log.info("P4 Module Starting for date:{}", date);
            applicationContext.getBean(AuditOrchestrator.class).processAudit(date);
        } catch (Exception e) {
            log.error("Error while processing Error:{}", e.getMessage(), e);
        }
        applicationContext.getBean(AlertServiceImpl.class).emptyAlertQueue();
        System.exit(0);
    }
}
