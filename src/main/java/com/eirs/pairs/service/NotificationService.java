package com.eirs.pairs.service;

import com.eirs.pairs.dto.NotificationDetailsDto;
import com.eirs.pairs.utils.notification.dto.NotificationResponseDto;

public interface NotificationService {
    NotificationResponseDto sendSms(NotificationDetailsDto notificationDetailsDto);

    NotificationResponseDto sendSmsInWindow(NotificationDetailsDto notificationDetailsDto);

}
