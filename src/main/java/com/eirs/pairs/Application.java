package com.eirs.pairs;

import com.eirs.pairs.alerts.AlertConfig;
import com.eirs.pairs.constants.UtilityType;
import com.eirs.pairs.p4.P4Process;
import com.eirs.pairs.reminder.GenericReminderNotificationProcess;
import com.eirs.pairs.service.UtilityFinderService;
import com.eirs.pairs.service.UtilityService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableEncryptableProperties
@Slf4j
public class Application implements CommandLineRunner {

    @Autowired
    private UtilityFinderService utilityFinderService;

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        UtilityType utilityType = null;
        try {
            utilityType = UtilityType.valueOf(args[0]);
            applicationContext.getBean(AlertConfig.class).getProcessId(utilityType);
        } catch (RuntimeException e) {
            log.error("Exception : {}", e.getMessage(), e);
            System.exit(0);
        } catch (Exception e) {
            log.error("Not able to locate Utility : {}", args[0], e);
            System.exit(0);
        }
        log.info("Run utility for : {}", utilityType);
        if (utilityType == UtilityType.P4_PROCESS) {
            LocalDate date = LocalDate.parse(args[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            log.info("Auto Pairing Mode Processing for date:{}", date);
            applicationContext.getBean(P4Process.class).executeQueries(date);
        } else if (utilityType == UtilityType.REMINDER_UTILITY) {
            GenericReminderNotificationProcess.processName = args[1];
            log.info("Generic Reminder Process for ProcessName:{}", GenericReminderNotificationProcess.processName);
            UtilityService utilityService = utilityFinderService.getUtility(utilityType);
            utilityService.runUtility();
        } else {
            UtilityService utilityService = utilityFinderService.getUtility(utilityType);
            utilityService.runUtility();
        }
        System.exit(0);
    }
}
