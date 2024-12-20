package com.eirs.duplicateExpiry.services;

import com.eirs.duplicateExpiry.repository.entity.ExceptionList;
import com.eirs.duplicateExpiry.repository.entity.ExceptionListHis;

import java.util.List;

public interface ExceptionListService {

    ExceptionList save(ExceptionList exceptionList);

    ExceptionListHis save(ExceptionListHis exceptionListHis);

    List<ExceptionList> getVIPImsi(String imsi);

    ExceptionList getNotVIPImeiAndImsi(String imei, String imsi);

    List<ExceptionList> getByImeiAndImsi(String imei, String imsi);

    void add(DuplicateDto fileDataDto);

    public void delete(DuplicateDto fileDataDto);
}
