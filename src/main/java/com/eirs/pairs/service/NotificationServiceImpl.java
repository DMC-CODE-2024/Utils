package com.eirs.pairs.service;

import com.eirs.pairs.constants.NotificationChannelType;
import com.eirs.pairs.dto.NotificationDetailsDto;
import com.eirs.pairs.exceptions.NotificationException;
import com.eirs.pairs.repository.OperatorSeriesRepository;
import com.eirs.pairs.repository.entity.OperatorSeries;
import com.eirs.pairs.utils.notification.NotificationUtils;
import com.eirs.pairs.utils.notification.dto.NotificationRequestDto;
import com.eirs.pairs.utils.notification.dto.NotificationResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NotificationUtils notificationUtils;

    @Autowired
    SmsConfigurationService smsConfigurationService;
    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @Autowired
    private OperatorSeriesRepository operatorSeriesRepository;

    @Override
    public NotificationResponseDto sendSms(NotificationDetailsDto notificationDetailsDto) {
        if (StringUtils.isBlank(notificationDetailsDto.getMsisdn()) || notificationDetailsDto.getSmsTag() == null) {
            throw new NotificationException("Msisdn or SmsTag can't be null");
        }
        NotificationRequestDto requestDto = getSmsNotificationRequestDto(notificationDetailsDto);
        NotificationResponseDto responseDto = null;
        if (requestDto != null) {
            if (requestDto.getOperatorName() == null) {
                log.info("sendSms Operator not found for notificationDetailsDto:{}", responseDto, notificationDetailsDto);
                return responseDto;
            }
            if (StringUtils.isBlank(requestDto.getMessage())) {
                log.info("sendSms Message not found for notificationDetailsDto:{}", responseDto, notificationDetailsDto);
                return responseDto;
            }
            requestDto.setChannelType(NotificationChannelType.SMS);
            responseDto = notificationUtils.sendNotification(requestDto);
            log.info("sendSms Notification Response:{} notificationDetailsDto:{}", responseDto, notificationDetailsDto);
        }
        return responseDto;
    }

    @Override
    public NotificationResponseDto sendSmsInWindow(NotificationDetailsDto notificationDetailsDto) {
        if (StringUtils.isBlank(notificationDetailsDto.getMsisdn()) || notificationDetailsDto.getSmsTag() == null) {
            throw new NotificationException("Msisdn or SmsTag can't be null");
        }
        NotificationRequestDto requestDto = getSmsNotificationRequestDto(notificationDetailsDto);

        NotificationResponseDto responseDto = null;
        if (requestDto != null) {
            if (requestDto.getOperatorName() == null) {
                log.info("sendSmsInWindow Operator not found for notificationDetailsDto:{}", responseDto, notificationDetailsDto);
                return responseDto;
            }
            if (StringUtils.isBlank(requestDto.getMessage())) {
                log.info("sendSmsInWindow Message not found for notificationDetailsDto:{}", responseDto, notificationDetailsDto);
                return responseDto;
            }
            requestDto.setChannelType(NotificationChannelType.SMS);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = LocalDateTime.of(LocalDate.now(), systemConfigurationService.getNotificationSmsStartTime());
            LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), systemConfigurationService.getNotificationSmsEndTime());
            if (now.isBefore(startDate)) {
                requestDto.setDeliveryDateTime(startDate);
            }
            if (now.isAfter(endDate)) {
                requestDto.setDeliveryDateTime(startDate.plusDays(1));
            }
            responseDto = notificationUtils.sendNotification(requestDto);
            log.info("sendSmsInWindow Notification Response:{} notificationDetailsDto:{}", responseDto, notificationDetailsDto);
        }
        return responseDto;
    }

    private NotificationRequestDto getEmailNotificationRequestDto(NotificationDetailsDto notificationDetailsDto) {
        String sms = smsConfigurationService.getSms(notificationDetailsDto.getSmsTag(), notificationDetailsDto.getSmsPlaceHolder(), notificationDetailsDto.getLanguage(), notificationDetailsDto.getModuleName());
        log.info("Mail for message:{} notificationDetailsDto:{} ", sms, notificationDetailsDto);
        NotificationRequestDto requestDto = new NotificationRequestDto();
        requestDto.setMessage(sms);
        requestDto.setFeatureName(notificationDetailsDto.getModuleName());
        requestDto.setSubFeature(notificationDetailsDto.getModuleName());
        requestDto.setFeatureTxnId(notificationDetailsDto.getRequestId());
        requestDto.setEmail(notificationDetailsDto.getEmailId());
        requestDto.setSubject("OTP for Pairing");
        requestDto.setMsgLang(notificationDetailsDto.getLanguage());
        requestDto.setSubFeature(notificationDetailsDto.getModuleName());
        requestDto.setFeatureTxnId(notificationDetailsDto.getRequestId());
        return requestDto;
    }

    private NotificationRequestDto getSmsNotificationRequestDto(NotificationDetailsDto notificationDetailsDto) {
        String sms = smsConfigurationService.getSms(notificationDetailsDto.getSmsTag(), notificationDetailsDto.getSmsPlaceHolder(), notificationDetailsDto.getLanguage(), notificationDetailsDto.getModuleName());
        log.info("SMS for sms:[{}] notificationDetailsDto:{}", sms, notificationDetailsDto);
        NotificationRequestDto requestDto = new NotificationRequestDto();
        requestDto.setMessage(sms);
        requestDto.setFeatureName(notificationDetailsDto.getModuleName());
        requestDto.setSubFeature(notificationDetailsDto.getModuleName());
        requestDto.setFeatureTxnId(notificationDetailsDto.getRequestId());
        requestDto.setMsisdn(notificationDetailsDto.getMsisdn());
        requestDto.setMsgLang(notificationDetailsDto.getLanguage());
        requestDto.setOperatorName(getOperator(notificationDetailsDto.getMsisdn()));
        return requestDto;
    }

    private String getOperator(String msisdn) {
        String seriesMsisdn = msisdn.substring(0, 5);
        log.info("Going to find Operator for msisdn:{} with series:{}", msisdn, seriesMsisdn);
        Optional<OperatorSeries> operator = operatorSeriesRepository.findAllWithCreationDateTimeBefore(Integer.parseInt(seriesMsisdn));
        if (operator.isPresent()) {
            log.info("Found Operator:{} for msisdn:{} with series:{}", operator.get(), msisdn, seriesMsisdn);
            return operator.get().getOperatorName();
        } else {
            log.info("Not Operator Found for msisdn:{} with series:{}", msisdn, seriesMsisdn);
            return "Default";
        }
    }
}
