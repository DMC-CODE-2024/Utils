package com.eirs.greyToBlack.services;

public interface QueriesConstants {
    String PARAM_DATE_TIME = "<DATE_TIME>";
    String PARAM_OPERATION = "<OPERATION>";

    String CURRENT_TIME = "<CURRENT_TIME>";

    String INSERT_INTO_GREY_HIS_TABLE = "insert into grey_list_his (actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,operation,TXN_ID,REQUEST_TYPE,SOURCE,remark) SELECT actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME," + CURRENT_TIME + "," + PARAM_OPERATION + ",TXN_ID,REQUEST_TYPE,SOURCE,remark from grey_list where expiry_date< '" + PARAM_DATE_TIME + "'";
    String ORACLE_INSERT_INTO_GREY_HIS_TABLE = "insert into grey_list_his (actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,operation,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON) SELECT actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on," + PARAM_OPERATION + ",TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON from  stolen_list_temp";

    String INSERT_INTO_BLACK_LIST_HIS_TABLE = "insert into black_list_his (actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,operation,TXN_ID,REQUEST_TYPE,SOURCE,remark) SELECT actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME," + CURRENT_TIME + "," + PARAM_OPERATION + ",TXN_ID,REQUEST_TYPE,SOURCE,remark from grey_list where expiry_date< '" + PARAM_DATE_TIME + "'";
    String ORACLE_INSERT_INTO_BLACK_LIST_HIS_TABLE = "insert into black_list_his (actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,operation,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON ) SELECT actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on," + PARAM_OPERATION + ",TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON  from  stolen_list_temp";

    String INSERT_INTO_BLACK_LIST_TABLE = "insert into black_list (actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE,remark ) SELECT actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE,remark  from grey_list where expiry_date< '" + PARAM_DATE_TIME + "'";

    String ORACLE_INSERT_INTO_BLACK_LIST_TABLE = "insert into black_list (actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON ) SELECT actual_imei,IMEI,IMSI,MSISDN,OPERATOR_NAME,created_on,TXN_ID,REQUEST_TYPE,SOURCE_OF_REQUEST,CLARIFY_REASON,REASON  from  stolen_list_temp";

    String DELETE_FROM_GREY_LIST_TABLE = "delete from grey_list where expiry_date< '" + PARAM_DATE_TIME + "'";

}
