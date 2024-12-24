package com.eirs.p4.constant;

public interface P4QueriesConstants {
    String PARAM_YYYYMMDD = "<yyyyMMdd>";
    String START_ID = "<START_ID>";

    String END_ID = "<END_ID>";
    String SELECT_MAX_ID_NO = "select max(id) from app.edr_" + PARAM_YYYYMMDD;

    String UPDATE_GSMA_LENGTH_INVALID = "update app.edr_" + PARAM_YYYYMMDD + " set is_gsma_valid=0 where ID>" + START_ID + " and ID<=" + END_ID + " and length(actual_imei) < 14";

    String UPDATE_GSMA_NON_NUMERIC_INVALID = "update app.edr_" + PARAM_YYYYMMDD + " set is_gsma_valid=0 where ID > " + START_ID + " and ID <= " + END_ID + " and actual_imei REGEXP '[a-zA-Z]'";

    String UPDATE_GSMA_AND_DEVICE_TYPE = "update app.edr_" + PARAM_YYYYMMDD + " A INNER JOIN mobile_device_repository B ON SUBSTRING(A.actual_imei, 1, 8) = B.device_id set A.is_gsma_valid=1 , A.device_type=B.device_type where A.ID >" + START_ID + " and A.ID <=" + END_ID;

    String UPDATE_DUPLICATE_IMEI_RECORDS_MYSQL = "update app.edr_" + PARAM_YYYYMMDD + " A INNER JOIN app.duplicate_imei B ON SUBSTRING(A.actual_imei, 1, 14) = B.imei set A.is_duplicate=1 where A.ID >" + START_ID + " and A.ID <=" + END_ID;

    String UPDATE_DUPLICATE_DEVICE_RECORDS_MYSQL = "update app.edr_" + PARAM_YYYYMMDD + " A INNER JOIN app.duplicate_device_detail B ON SUBSTRING(A.actual_imei, 1, 14) = B.imei set A.is_duplicate=2 where A.ID >" + START_ID + " and A.ID <=" + END_ID + " and A.imsi=B.imsi";

    String UPDATE_GSMA_LENGTH_INVALID_ORACLE = "update app.edr_" + PARAM_YYYYMMDD + " set is_gsma_valid=0 where length(actual_imei) < 14";

    String UPDATE_GSMA_NON_NUMERIC_INVALID_ORACLE = "update app.edr_" + PARAM_YYYYMMDD + " set is_gsma_valid=0 where  REGEXP_LIKE(actual_imei,'[a-zA-Z]')";

    String UPDATE_IS_GSMA_WITH_INVALID_IMEI_MYSQL = "update app.edr_" + PARAM_YYYYMMDD + " A INNER JOIN app.eirs_invalid_imei B ON SUBSTRING(A.actual_imei, 1, 14) = B.imei set A.is_gsma_valid=0 where A.ID >" + START_ID + " and A.ID <=" + END_ID ;

    String UPDATE_IS_PAIRED_RECORDS_MYSQL = "update app.edr_" + PARAM_YYYYMMDD + " A INNER JOIN app.imei_pair_detail B ON SUBSTRING(A.actual_imei, 1, 14) = B.imei set A.is_paired=1 where A.ID >" + START_ID + " and A.ID <=" + END_ID + " and A.imsi=B.imsi and B.pair_mode='AUTO'";

    String UPDATE_GSMA_AND_DEVICE_TYPE_ORACLE_1 = "update app.edr_" + PARAM_YYYYMMDD + " A SET A.device_type = (SELECT device_type FROM mobile_device_repository B WHERE SUBSTR(A.actual_imei, 1, 8) = B.device_id )";

    String UPDATE_GSMA_AND_DEVICE_TYPE_ORACLE_2 = "update app.edr_" + PARAM_YYYYMMDD + " set is_gsma_valid =1 where actual_imei in (select distinct(A.actual_imei) from app.edr_" + PARAM_YYYYMMDD + " A , mobile_device_repository B where SUBSTR(A.actual_imei, 1, 8) = B.device_id)";

    String UPDATE_CUSTOM_FLAG = "update app.edr_" + PARAM_YYYYMMDD + " A INNER JOIN national_whitelist B ON SUBSTRING(A.actual_imei, 1, 14) = B.imei set A.is_custom_paid=1  where A.ID >" + START_ID + " and A.ID <=" + END_ID + " and  A.is_gsma_valid=1 and B.gdce_imei_status > 0";

    String UPDATE_CUSTOM_FLAG_ORACLE = "update app.edr_" + PARAM_YYYYMMDD + " set is_custom_paid=1 where actual_imei in (select distinct(A.actual_imei) from app.edr_" + PARAM_YYYYMMDD + " A , national_whitelist B where SUBSTR(A.actual_imei, 1, 14) = B.imei and B.gdce_imei_status > 0)";

    String UPDATE_MSISDN = "update app.edr_" + PARAM_YYYYMMDD + " A INNER JOIN active_msisdn_list B ON A.imsi = B.imsi set A.msisdn=B.msisdn where A.ID >" + START_ID + " and A.ID <=" + END_ID;

    String UPDATE_MSISDN_ORACLE = "update app.edr_" + PARAM_YYYYMMDD + " A SET A.msisdn=(SELECT MSISDN from active_msisdn_list B WHERE A.imsi = B.imsi )";

    String UPDATE_OPERATOR = "update app.edr_" + PARAM_YYYYMMDD + " A INNER JOIN operator_series B ON SUBSTRING(A.msisdn,1,5) <= B.series_start and SUBSTRING(A.msisdn,1,5) >= B.series_end and series_type='msisdn' set A.operator_name=B.operator_name where A.ID >" + START_ID + " and A.ID <=" + END_ID;

    String UPDATE_OPERATOR_ORACLE = "update app.edr_" + PARAM_YYYYMMDD + " A SET A.operator_name=(select operator_name from operator_series B where SUBSTR(A.msisdn,1,5) <= B.series_start and SUBSTR(A.msisdn,1,5) >= B.series_end and series_type='msisdn')";

    String DROP_EDR_TABLE = "drop table app.edr_" + PARAM_YYYYMMDD;

    String DROP_EDR_TABLE_ORACLE = "drop table app.edr_" + PARAM_YYYYMMDD;
    String CREATE_EDR_TABLE_MYSQL = "CREATE TABLE app.edr_" + PARAM_YYYYMMDD + " (" +
            "  id bigint NOT NULL AUTO_INCREMENT," +
            "  edr_date_time timestamp DEFAULT NULL," +
            "  imei_arrival_time timestamp DEFAULT NULL," +
            "  created_on timestamp DEFAULT CURRENT_TIMESTAMP," +
            "  actual_imei varchar(20) DEFAULT NULL," +
            "  imsi varchar(20) DEFAULT NULL," +
            "  msisdn varchar(15) DEFAULT NULL," +
            "  operator_name varchar(50) DEFAULT NULL," +
            "  file_name varchar(250) DEFAULT NULL," +
            "  is_gsma_valid int DEFAULT 0," +
            "  is_duplicate int DEFAULT 0," +
            "  is_paired int DEFAULT 0," +
            "  is_invalid_imei int DEFAULT 0," +
            "  is_custom_paid int DEFAULT 0," +
            "  tac varchar(20) DEFAULT NULL," +
            "  device_type varchar(50) DEFAULT NULL," +
            "  source varchar(50) DEFAULT NULL," +
            "  protocol varchar(50) DEFAULT NULL," +
            "  UNIQUE (actual_imei,imsi)," +
            "  PRIMARY KEY (id)" +
            ") ENGINE=InnoDB;";

    String CREATE_EDR_TABLE_ORACLE = "CREATE TABLE app.edr_" + PARAM_YYYYMMDD + " (  " +
            "ID number(19,0)  GENERATED BY DEFAULT ON NULL AS IDENTITY, " +
            "edr_date_time timestamp(6) default NULL, " +
            "imei_arrival_time timestamp(6) DEFAULT NULL," +
            "created_on timestamp(6) DEFAULT SYSTIMESTAMP," +
            "actual_imei varchar2(20 char), " +
            "imsi varchar2(20 char), " +
            "msisdn varchar2(20 char), " +
            "operator_name varchar2(50 char), " +
            "protocol varchar2(50 char), " +
            "source varchar2(50 char), " +
            "is_duplicate number(2,0) DEFAULT 0," +
            "is_paired number(2,0) DEFAULT 0," +
            "is_invalid_imei number(2,0) DEFAULT 0," +
            "file_name varchar2(250 char), " +
            "is_gsma_valid number(2,0) DEFAULT 0, " +
            "is_custom_paid number(2,0) DEFAULT 0, " +
            "tac varchar2(20 char), " +
            "device_type varchar2(50 char), " +
            "UNIQUE (actual_imei,imsi)," +
            "PRIMARY KEY (id))";
    String CREATE_INDEX_TAC_MYSQL = "ALTER TABLE app.edr_" + PARAM_YYYYMMDD + " ADD INDEX((SUBSTRING(actual_imei,1,8)))";
    String CREATE_INDEX_IMEI_LENGTH_MYSQL = "ALTER TABLE app.edr_" + PARAM_YYYYMMDD + " ADD INDEX((length(actual_imei)))";
    String CREATE_INDEX_IMEI_MYSQL = "ALTER TABLE app.edr_" + PARAM_YYYYMMDD + " ADD INDEX((SUBSTRING(actual_imei,1,14)))";
    String CREATE_INDEX_MSISDN_MYSQL = "ALTER TABLE app.edr_" + PARAM_YYYYMMDD + " ADD INDEX((SUBSTRING(msisdn,1,5)))";
    String CREATE_INDEX_ACTUAL_IMEI_MYSQL = "ALTER TABLE app.edr_" + PARAM_YYYYMMDD + " ADD INDEX(actual_imei)";
    String CREATE_INDEX_IS_DUPLICATE_MYSQL = "ALTER TABLE app.edr_" + PARAM_YYYYMMDD + " ADD INDEX(is_duplicate)";
    String CREATE_INDEX_IS_PAIRED_MYSQL = "ALTER TABLE app.edr_" + PARAM_YYYYMMDD + " ADD INDEX(is_paired)";
    String CREATE_INDEX_IS_GSMA_VALID_MYSQL = "ALTER TABLE app.edr_" + PARAM_YYYYMMDD + " ADD INDEX(is_gsma_valid)";
    String CREATE_INDEX_DEVICE_ACTUAL_IMEI_MYSQL = "ALTER TABLE app.edr_" + PARAM_YYYYMMDD + " ADD INDEX(device_type,actual_imei)";


    String CREATE_INDEX_TAC_ORACLE = "create index edr_" + PARAM_YYYYMMDD + "_tac on app.edr_" + PARAM_YYYYMMDD + " (SUBSTR(actual_imei,1,8))";

    String CREATE_INDEX_IMEI_ORACLE = "create index edr_" + PARAM_YYYYMMDD + "_imei on app.edr_" + PARAM_YYYYMMDD + " (SUBSTR(actual_imei,1,14))";
    String CREATE_INDEX_IMEI_LENGTH_ORACLE = "create index edr_" + PARAM_YYYYMMDD + "_imei on app.edr_" + PARAM_YYYYMMDD + " (SUBSTR(actual_imei,1,14))";
    String CREATE_INDEX_MSISDN_ORACLE = "create index edr_" + PARAM_YYYYMMDD + "_msisdn on app.edr_" + PARAM_YYYYMMDD + " (SUBSTR(msisdn,1,5))";
    String CREATE_INDEX_ACTUAL_IMEI_ORACLE = "create index edr_" + PARAM_YYYYMMDD + "_actual_imei on app.edr_" + PARAM_YYYYMMDD + " (actual_imei)";

    String CREATE_INDEX_DEVICE_ACTUAL_IMEI_ORACLE = "create index edr_" + PARAM_YYYYMMDD + "_actual_imei on app.edr_" + PARAM_YYYYMMDD + " (device_type,actual_imei)";
}
