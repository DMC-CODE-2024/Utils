package com.eirs.pairs.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public interface DateFormatterConstants {
    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateFormat reminderNotificationSmsDateFormat = new SimpleDateFormat("dd MMM yyyy");
}
