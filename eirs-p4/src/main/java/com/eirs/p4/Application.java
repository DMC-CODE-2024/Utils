package com.eirs.p4;

import com.eirs.p4.services.P4Process;
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
@EnableJpaRepositories({"com.eirs.p4","com.eirs.repository"})
@EntityScan({"com.eirs.p4","com.eirs.repository"})
@ComponentScan({"com.eirs"})
public class Application implements CommandLineRunner {

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
//            LocalDate date = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate date = LocalDate.of(2024, 9, 05);
            log.info("P4 Module Starting for date:{}", date);
            applicationContext.getBean(P4Process.class).executeQueries(date);
        } catch (Exception e) {
            log.error("Error while processing Error:{}", e.getMessage(), e);
        }
        System.exit(0);
    }
}