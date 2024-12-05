package com.eirs.audit.services;

import com.eirs.audit.model.EirsData;

import java.nio.file.FileSystemException;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditProcess {


    List<EirsData> process(Integer eirNumber, LocalDateTime startDate, LocalDateTime endDate, String operator) throws Exception;
}
