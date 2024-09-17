package com.eirs.pairs.tempNationlaWhitelistreminder;

public interface ReminderQueriesConstants {
    String PARAM_START_RANGE = "<START_RANGE>";

    String PARAM_END_RANGE = "<END_RANGE>";
    String PARAM_REMINDER_STATUS = "<REMINDER_STATUS>";

    String PARAM_NATIONAL_WHITELIST_ID = "<NATIONAL_WHITELIST_ID>";
    String UPDATE_NATIONAL_WHITELIST = "update temp_national_whitelist set reminder_status=" + PARAM_REMINDER_STATUS + " where NATIONAL_WHITELIST_ID=" + PARAM_NATIONAL_WHITELIST_ID;

    String SELECT_MYSQL_TEMP_NATIONAL_WHITELIST = "select * from temp_national_whitelist  where reminder_status=" + PARAM_REMINDER_STATUS + " and CREATED_ON_DATE <'" + PARAM_START_RANGE;

    String SELECT_ORACLE_TEMP_NATIONAL_WHITELIST = "select * from temp_national_whitelist where reminder_status=" + PARAM_REMINDER_STATUS + " and CREATED_ON_DATE < to_date( '" + PARAM_START_RANGE + "', 'YYYY-MM-DD HH24:MI:SS' )";

}
