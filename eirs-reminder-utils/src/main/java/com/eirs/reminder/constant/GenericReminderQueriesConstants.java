package com.eirs.reminder.constant;

public interface GenericReminderQueriesConstants {
    String PARAM_START_RANGE = "<START_RANGE>";

    String PARAM_END_RANGE = "<END_RANGE>";

    String TABLE_NAME = "<TABLE_NAME>";

    String WHERE_CLAUSE = "<WHERE_CLAUSE>";
    String PARAM_REMINDER_STATUS = "<REMINDER_STATUS>";

    String ID = "<ID>";

    String WHERE_ID = "<WHERE_ID>";
    String UPDATE_NATIONAL_WHITELIST = "update " + TABLE_NAME + " set reminder_status=" + PARAM_REMINDER_STATUS + " where " + WHERE_ID + "=" + ID;

    String SELECT_MYSQL_REMINDER_TABLE = "select * from " + TABLE_NAME + " where reminder_status=" + PARAM_REMINDER_STATUS + " and CREATED_ON <'" + PARAM_START_RANGE + "' and " + WHERE_CLAUSE;

    String SELECT_ORACLE_REMINDER_TABLE = "select * from " + TABLE_NAME + "  where reminder_status=" + PARAM_REMINDER_STATUS + " and CREATED_ON < to_date( '" + PARAM_START_RANGE + "', 'YYYY-MM-DD HH24:MI:SS' ) and " + WHERE_CLAUSE;

}
