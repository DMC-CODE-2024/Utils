package com.eirs.pairClean.services;

public interface PairCleanQueriesConstants {
    String PARAM_START_RANGE = "<START_RANGE>";

    String PARAM_ID = "<ID>";
    String MYSQL_INSERT_INTO_HISTORY = "INSERT into app.imei_pair_detail_his (file_name,gsma_status,pairing_date,record_time,msisdn,imei,imsi,operator,allowed_days,expiry_date,pair_mode,actual_imei,txn_id,action,action_remark) " + "SELECT file_name,gsma_status,pairing_date,record_time,msisdn,imei,imsi,operator,allowed_days,expiry_date,pair_mode,actual_imei,txn_id,'DELETE','CLEANUP' from app.imei_pair_detail where id=" + PARAM_ID;
    String ORACLE_INSERT_INTO_HISTORY = "INSERT into app.imei_pair_detail_his (file_name,gsma_status,pairing_date,record_time,msisdn,imei,imsi,operator,allowed_days,expiry_date,pair_mode,actual_imei,txn_id,action,action_remark) " + "SELECT file_name,gsma_status,pairing_date,record_time,msisdn,imei,imsi,operator,allowed_days,expiry_date,pair_mode,actual_imei,txn_id,'DELETE','CLEANUP' from app.imei_pair_detail  where id=" + PARAM_ID;

    String MYSQL_DELETE_PAIRING_TABLE = "delete from app.imei_pair_detail where id=" + PARAM_ID;
    String ORACLE_DELETE_PAIRING_TABLE = "delete from app.imei_pair_detail where id=" + PARAM_ID;

    String SELECT_PAIR_MYSQL = "SELECT id,file_name,gsma_status,pairing_date,record_time,msisdn,imei,imsi,operator,allowed_days,expiry_date,pair_mode,actual_imei,txn_id,'DELETE','CLEANUP' from app.imei_pair_detail where record_time IS NULL and pairing_date <'" + PARAM_START_RANGE + "'";
    String SELECT_PAIR_ORACLE = "SELECT id,file_name,gsma_status,pairing_date,record_time,msisdn,imei,imsi,operator,allowed_days,expiry_date,pair_mode,actual_imei,txn_id,'DELETE','CLEANUP' from app.imei_pair_detail  where record_time IS NULL and pairing_date <  to_date('" + PARAM_START_RANGE + "', 'YYYY-MM-DD HH24:MI:SS')";
}
