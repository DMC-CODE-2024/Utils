package com.eirs.services;

import com.eirs.utils.notification.dto.NotificationDetailsDto;
import com.eirs.utils.notification.dto.NotificationResponseDto;

public interface NotificationService {
    NotificationResponseDto sendSms(NotificationDetailsDto notificationDetailsDto);

    NotificationResponseDto sendSmsInWindow(NotificationDetailsDto notificationDetailsDto);

}
