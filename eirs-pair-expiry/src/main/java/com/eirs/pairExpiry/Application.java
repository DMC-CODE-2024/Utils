package com.eirs.pairExpiry;

import com.eirs.pairExpiry.services.PairingExpiryProcess;
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
@EnableJpaRepositories({"com.eirs.pairExpiry","com.eirs.repository"})
@EntityScan({"com.eirs.pairExpiry","com.eirs.repository"})
@ComponentScan({"com.eirs.pairExpiry", "com.eirs"})
public class Application {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);
        try {
            context.getBean(PairingExpiryProcess.class).runUtility();
        } catch (Exception e) {
            log.error("Error while processing Error:{}", e.getMessage(), e);
        }
        System.exit(0);
    }

}
