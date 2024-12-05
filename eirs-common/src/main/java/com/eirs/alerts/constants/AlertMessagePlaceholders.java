package com.eirs.alerts.constants;

public enum AlertMessagePlaceholders {
    EXCEPTION("param_exception"),

    CONFIG_KEY("param_key"), CONFIG_VALUE("param_value"),

    FEATURE_NAME("param_feature"), LANGUAGE("param_language"), SMS("param_sms"),
    FILENAME("param_filename"), OPERATOR("param_operator"), EIR_NUMBER("param_eirNumber");;

    String placeholder;

    AlertMessagePlaceholders(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }
}
