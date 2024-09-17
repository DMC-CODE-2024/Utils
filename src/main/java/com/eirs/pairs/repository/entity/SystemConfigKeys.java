package com.eirs.pairs.repository.entity;

public interface SystemConfigKeys {

    String featureName = "Pairing";
    String manual_pair_clean_up_days = "manual_pair_clean_up_days";

    String mgmt_init_start_clean_up_hours = "mgmt_init_start_clean_up_hours";

    String stolen_grey_to_black_list_days = "stolen_grey_to_black_list_days";

    String reminder_first_notification_days = "reminder_first_notification_days";

    String reminder_second_notification_days = "reminder_second_notification_days";

    String reminder_third_notification_days = "reminder_third_notification_days";

    String edr_table_clean_days = "edr_table_clean_days";

    String generic_reminder_table_name = "reminder_table_name";

    String generic_reminder_where_clause = "reminder_where_clause";

    String generic_reminder_first_notification_days = "reminder_first_notification_days";

    String generic_reminder_second_notification_days = "reminder_second_notification_days";

    String generic_reminder_third_notification_days = "reminder_third_notification_days";
    String notification_sms_start_time = "notification_sms_start_time";

    String notification_sms_end_time = "notification_sms_end_time";
    String default_language = "default_lang";
}
