package com.eirs.audit;

import com.eirs.alerts.AlertServiceImpl;
import com.eirs.audit.orchestrator.AuditOrchestrator;
import com.eirs.audit.services.SystemConfigurationService;
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
import java.util.concurrent.TimeUnit;

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
//            LocalDate date = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate date = LocalDate.now();
            log.info("Audit Module Starting for date:{}", date);
            String selectedOperator = null;
            try {
                String opArgs = args[1];
                if (AuditOrchestrator.ALL_OPERATOR.equalsIgnoreCase(opArgs))
                    selectedOperator = AuditOrchestrator.ALL_OPERATOR;
                else {
                    applicationContext.getBean(SystemConfigurationService.class).getOperators().stream()
                            .filter(operator -> opArgs.equalsIgnoreCase(operator))
                            .findAny().orElseThrow(() -> new Exception("Operator Arg is wrong / doesn't Exist"));
                    selectedOperator = opArgs;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                selectedOperator = AuditOrchestrator.ALL_OPERATOR;
            }
            applicationContext.getBean(AuditOrchestrator.class).processAudit(date, selectedOperator);
        } catch (Exception e) {
            log.error("Error while processing Error:{}", e.getMessage(), e);
        }
        applicationContext.getBean(AlertServiceImpl.class).emptyAlertQueue();
        try {
            TimeUnit.SECONDS.sleep(1);
            System.exit(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
