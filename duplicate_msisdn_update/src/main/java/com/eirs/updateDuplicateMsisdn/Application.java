package com.eirs.updateDuplicateMsisdn;

import com.eirs.alerts.AlertServiceImpl;
import com.eirs.updateDuplicateMsisdn.services.DuplicateUpdateMsisdnProcess;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableEncryptableProperties
@Slf4j
@EnableJpaRepositories({"com.eirs.updateDuplicateMsisdn", "com.eirs.repository"})
@EntityScan({"com.eirs.updateDuplicateMsisdn", "com.eirs.repository"})
@ComponentScan({"com.eirs.updateDuplicateMsisdn", "com.eirs"})
public class Application {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);
        try {
            context.getBean(DuplicateUpdateMsisdnProcess.class).runUtility();
        } catch (Exception e) {
            log.error("Error while processing Error:{}", e.getMessage(), e);
        }
        context.getBean(AlertServiceImpl.class).emptyAlertQueue();
        try {
            TimeUnit.SECONDS.sleep(1);
            System.exit(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
