package com.eirs.updateDuplicateMsisdn;

import com.eirs.updateDuplicateMsisdn.services.DuplicateUpdateMsisdnProcess;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
        System.exit(0);
    }
}
