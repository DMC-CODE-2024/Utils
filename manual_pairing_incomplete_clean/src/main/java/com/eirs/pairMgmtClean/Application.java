package com.eirs.pairMgmtClean;

import com.eirs.pairMgmtClean.service.PairMgmtInitStartCleanProcess;
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
@EnableJpaRepositories({"com.eirs.pairMgmtClean", "com.eirs.repository"})
@EntityScan({"com.eirs.pairMgmtClean", "com.eirs.repository"})
@ComponentScan({"com.eirs.pairMgmtClean", "com.eirs"})
public class Application {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);
        try {
            context.getBean(PairMgmtInitStartCleanProcess.class).runUtility();
        } catch (Exception e) {
            log.error("Error while processing Error:{}", e.getMessage(), e);
        }
        System.exit(0);
    }
}