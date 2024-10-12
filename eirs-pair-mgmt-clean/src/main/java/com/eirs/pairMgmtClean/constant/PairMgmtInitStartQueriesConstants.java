package com.eirs.pairMgmtClean.constant;

public interface PairMgmtInitStartQueriesConstants {

    String PARAM_START_RANGE = "<START_RANGE>";

    String MYSQL_UPDATE_MGMT_INIT_TABLE = "update app.imei_manual_pair_mgmt set status='CANCEL' where status='INIT_START' and created_on <'" + PARAM_START_RANGE + "'";

}
