package com.eirs.constants;

public enum SmsTag {
    NationalWhitelistReminder1Sms("", ""), NationalWhitelistReminder2Sms("", ""), NationalWhitelistReminder3Sms("", ""), DuplicateSms("", ""),

    GenericReminder1Sms("", ""), GenericReminder2Sms("", ""),

    GenericReminder3Sms("", ""), AutoPairGsmaInvalidSMS("", ""), AutoPairGsmaValidSMS("", ""), PairCleanUpSms("", "");

    private String httpResp;

    private String description;

    private SmsTag(String httpResp, String description) {
        this.description = description;
        this.httpResp = httpResp;
    }

    public String getDescription() {
        return description;
    }

    public String getHttpResp() {
        return httpResp;
    }
}
