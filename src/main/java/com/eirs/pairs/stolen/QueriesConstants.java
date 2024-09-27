package com.eirs.pairs.stolen;

public interface QueriesConstants {
    String PARAM_DATE_TIME = "<DATE_TIME>";
    String PARAM_OPERATION = "<OPERATION>";

    String CURRENT_TIME = "<CURRENT_TIME>";

    String MYSQL_SELECT_GREY_LIST_EXIST_IN_STOLEN_TABLE = "insert into  stolen_list_temp (ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON) SELECT ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME," + CURRENT_TIME + ",TXN_ID,REQUEST_TYPE,SOURCE,remark from grey_list where created_on< '" + PARAM_DATE_TIME + "' and actual_imei in (SELECT IMEI from stolen_device_detail)";

    String ORACLE_SELECT_GREY_LIST_EXIST_IN_STOLEN_TABLE = "insert into  stolen_list_temp (ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON) SELECT ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME," + CURRENT_TIME + ",TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON from grey_list where created_on < to_date('" + PARAM_DATE_TIME + "', 'YYYY-MM-DD HH24:MI:SS') and actual_imei in (SELECT IMEI from stolen_device_detail)";

   String INSERT_INTO_GREY_HIS_TABLE = "insert into grey_list_his (ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,operation,TXN_ID,REQUEST_TYPE,SOURCE,remark) SELECT ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on," + PARAM_OPERATION + ",TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON from  stolen_list_temp";
    String ORACLE_INSERT_INTO_GREY_HIS_TABLE = "insert into grey_list_his (ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,operation,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON) SELECT ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on," + PARAM_OPERATION + ",TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON from  stolen_list_temp";

    String INSERT_INTO_BLACK_LIST_HIS_TABLE = "insert into black_list_his (ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,operation,TXN_ID,REQUEST_TYPE,SOURCE,remark ) SELECT ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on," + PARAM_OPERATION + ",TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON  from  stolen_list_temp";
    String ORACLE_INSERT_INTO_BLACK_LIST_HIS_TABLE = "insert into black_list_his (ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,operation,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON ) SELECT ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on," + PARAM_OPERATION + ",TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON  from  stolen_list_temp";

    String INSERT_INTO_BLACK_LIST_TABLE = "insert into black_list (ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE,remark ) SELECT ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON  from  stolen_list_temp";

    String ORACLE_INSERT_INTO_BLACK_LIST_TABLE = "insert into black_list (ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON ) SELECT ID,actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON  from  stolen_list_temp";

    String DELETE_FROM_GREY_LIST_TABLE = "delete from grey_list where ID IN (select ID from  stolen_list_temp)";

    String TRUNCATE_GREY_LIST_TEMP = "truncate table  stolen_list_temp";

}
