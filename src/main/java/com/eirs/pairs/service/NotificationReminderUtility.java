package com.eirs.pairs.service;

import com.eirs.pairs.tempNationlaWhitelistreminder.ReminderNotificationProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationReminderUtility implements UtilityService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReminderNotificationProcess reminderNotificationProcess;

    @Override
    public void runUtility() {
        log.info("Starting National Whitelist Reminder Notification process");
        reminderNotificationProcess.process();
    }

}
