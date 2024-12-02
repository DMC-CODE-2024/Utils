package com.eirs.blacklist.constant;

public interface QueriesConstants {
    String PARAM_START_RANGE = "<START_RANGE>";

    String PARAM_END_RANGE = "<END_RANGE>";

    String QUERY_1 = "insert into aud.black_identify_aud (source,operator,actual_imei,imei,msisdn,tac,file_name,input_source,imsi,blocked_source,blocked_date) SELECT B.source , B.operator_name ,B.actual_imei,B.imei,B.msisdn,B.tac,'','NETWORD_EDR',B.imsi,'IME_IMSI',B.created_on FROM app.black_list B INNER JOIN app.active_unique_imei E ON E.CREATED_ON >='" + PARAM_START_RANGE + "' and E.CREATED_ON <'" + PARAM_END_RANGE + "' and B.imei=E.imei and B.imsi=E.imsi and B.msisdn=E.msisdn";


    String QUERY_2 = "insert into aud.black_identify_aud (source,operator,actual_imei,imei,msisdn,tac,file_name,input_source,imsi,blocked_source,blocked_date) SELECT B.source , B.operator_name ,B.actual_imei,B.imei,B.msisdn,B.tac,'','NETWORD_EDR',B.imsi,'IME_IMSI',B.created_on FROM app.black_list B INNER JOIN app.active_imei_with_different_msisdn E ON E.CREATED_ON >='" + PARAM_START_RANGE + "' and E.CREATED_ON <'" + PARAM_END_RANGE + "' and B.imei=E.imei and B.imsi=E.imsi and B.msisdn=E.msisdn";


    String QUERY_3 = "insert into aud.black_identify_aud (source,operator,actual_imei,imei,msisdn,tac,file_name,input_source,imsi,blocked_source,blocked_date) SELECT B.source , B.operator_name ,B.actual_imei,B.imei,B.msisdn,B.tac,'','EIR_CDR',B.imsi,'IME_IMSI',B.created_on FROM app.black_list B INNER JOIN app_edr.active_unique_imei E ON E.CREATED_ON >='" + PARAM_START_RANGE + "' and E.CREATED_ON <'" + PARAM_END_RANGE + "' and B.imei=E.imei and B.imsi=E.imsi and B.msisdn=E.msisdn";


    String QUERY_4 = "insert into aud.black_identify_aud (source,operator,actual_imei,imei,msisdn,tac,file_name,input_source,imsi,blocked_source,blocked_date) SELECT B.source , B.operator_name ,B.actual_imei,B.imei,B.msisdn,B.tac,'','EIR_CDR',B.imsi,'IME_IMSI',B.created_on FROM app.black_list B INNER JOIN app_edr.active_imei_with_different_imsi E ON E.CREATED_ON >='" + PARAM_START_RANGE + "' and E.CREATED_ON <'" + PARAM_END_RANGE + "' and B.imei=E.imei and B.imsi=E.imsi and B.msisdn=E.msisdn";


    String QUERY_5 = "update aud.black_identify_aud A INNER JOIN app.exception_list B ON  A.imei = B.imei set A.is_whitelist=1 where A.imsi=B.imsi and A.msisdn=B.msisdn";

}
