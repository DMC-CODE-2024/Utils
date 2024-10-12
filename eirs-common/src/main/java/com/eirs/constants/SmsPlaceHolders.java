package com.eirs.constants;

public enum SmsPlaceHolders {
    ACTUAL_IMEI("<ACTUAL_IMEI>"), IMEI("<IMEI>"), REQUEST_ID("<REQUEST_ID>"), MSISDN("<MSISDN>"), IMSI("<IMSI>"), OPERATOR("<OPERATOR>"), OTP("<OTP>"), DATE_DD_MMM_YYYY("<DATE_DD_MMM_YYYY>");
    private String placeHolder;

    SmsPlaceHolders(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public String getPlaceHolder() {
        return this.placeHolder;
    }
}
