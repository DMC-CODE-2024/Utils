package com.eirs.reminder;

import com.eirs.alerts.AlertServiceImpl;
import com.eirs.reminder.services.GenericReminderNotificationProcess;
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
@EnableJpaRepositories({"com.eirs.reminder", "com.eirs.repository"})
@EntityScan({"com.eirs.reminder", "com.eirs.repository"})
@ComponentScan({"com.eirs.reminder", "com.eirs"})
public class Application implements CommandLineRunner {

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        try {
            GenericReminderNotificationProcess.processName = args[0];
            log.info("Generic Reminder Process for ProcessName:{}", GenericReminderNotificationProcess.processName);
            applicationContext.getBean(GenericReminderNotificationProcess.class).process();
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
