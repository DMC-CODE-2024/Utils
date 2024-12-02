package com.eirs.audit.util;

import com.eirs.audit.constant.DateFormatterConstants;
import com.eirs.audit.constant.FileType;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

public class FileNameUtil {
    private static String getFilename(LocalDateTime startDate, LocalDateTime endDate, FileType fileType, String filePrefix) {
        String filename = "";
        switch (fileType) {
            case DAILY_FULL -> {
                filename = filePrefix + "_FULL_" + endDate.format(DateFormatterConstants.fileSuffixDateFormat);
            }
            case DAILY_INCREMENTAL -> {
                filename = filePrefix + "_INCREMENTAL_" + endDate.format(DateFormatterConstants.fileSuffixDateFormat);
            }
            case WEEKLY_FULL -> {
                filename = filePrefix + "_FULL_" + fileType.getValue().toUpperCase() + "_" + endDate.format(DateFormatterConstants.fileSuffixDateFormat);
            }
            case WEEKLY_INCREMENTAL -> {
                filename = filePrefix + "_INCREMENTAL_" + fileType.getValue().toUpperCase() + "_" + endDate.format(DateFormatterConstants.fileSuffixDateFormat);
            }
        }
        return filename + ".csv";
    }

    public static String getFilename(LocalDateTime startDate, LocalDateTime endDate, FileType fileType, String filePrefix, String shortCode) {
        if (StringUtils.isBlank(shortCode))
            return getFilename(startDate, endDate, fileType, filePrefix);
        return shortCode + "_" + getFilename(startDate, endDate, fileType, filePrefix);
    }
}
