package com.eirs.audit.mapper;

import com.eirs.audit.model.EirsData;
import com.eirs.audit.repository.entity.EirlistOutputAudit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuditTableMapper {

    public static List<EirlistOutputAudit> toEntity(List<EirsData> listData) {
        List<EirlistOutputAudit> list = new ArrayList<>();
        for (EirsData data : listData) {
            EirlistOutputAudit audit = new EirlistOutputAudit();
            audit.setActualImei(data.getActualImei());
            audit.setTac(data.getTac());
            audit.setImei(data.getImei());
            audit.setImsi(data.getImsi());
            audit.setMsisdn(data.getMsisdn());
            audit.setOperator(data.getOperator());
            audit.setCreatedOn(new java.util.Date());
            audit.setModifiedOn(new Date());
            audit.setMissingSource(data.getMissingSource());
            audit.setFileName(data.getFileName());
            audit.setListName(data.getListName());
            audit.setBlockedDate(data.getListDate());
            audit.setEirNo(data.getEirNumber());
            list.add(audit);
        }
        return list;

    }
}
