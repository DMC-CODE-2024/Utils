package com.eirs.utils.notification.dto;

import com.eirs.constants.NotificationLanguage;
import com.eirs.constants.SmsPlaceHolders;
import com.eirs.constants.SmsTag;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class NotificationDetailsDto {

    private String msisdn;
    private String emailId;
    private Map<SmsPlaceHolders, String> smsPlaceHolder;
    private String smsTag;
    private NotificationLanguage language;
    private String moduleName;
    private LocalTime startTime;
    private LocalTime endTime;
    private String requestId;

}
