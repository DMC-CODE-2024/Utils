package com.eirs.audit.constant;

import java.time.format.DateTimeFormatter;

public interface DateFormatterConstants {
    DateTimeFormatter eirFilePreDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter fileSuffixDateFormat = DateTimeFormatter.ofPattern("yyyyMMddHH");
    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
