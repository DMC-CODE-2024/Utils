package com.eirs.pairs.service;

import com.eirs.pairs.reminder.GenericReminderNotificationProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenericReminderUtility implements UtilityService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GenericReminderNotificationProcess genericReminderNotificationProcess;

    @Override
    public void runUtility() {
        log.info("Starting Generic Reminder Notification process");
        genericReminderNotificationProcess.process();
    }

}
