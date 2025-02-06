package com.eirs.audit.model;

import com.eirs.audit.repository.entity.EirlistOutputAuditConstants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class EirsData {

    private String actualImei;

    private String imei;

    private String imsi;

    private String msisdn;

    private String tac;

    private String listName;

    private String fileName;

    private String missingSource;

    private String operator;

    private Date listDate;

    private Integer eirNumber;

    public EirsData(String data, String listName, String missingSource, String operator, String fileName, Integer eirNumber) {
        this.listName = listName;
        this.missingSource = missingSource;
        this.operator = operator;
        this.fileName = fileName;
        this.eirNumber = eirNumber;
        String[] d = data.split(",");
        if (EirlistOutputAuditConstants.BLOCKED_TAC_NAME.equals(listName)) {
            this.tac = d[0];
        } else {
            this.actualImei = d[0];
            try {
                this.imei = actualImei.substring(0, 14);
            } catch (Exception e) {
                this.imei = actualImei;
            }
            try {
                this.imsi = StringUtils.isBlank(d[1]) ? null : d[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                this.imsi = null;
            }

            try {
                this.msisdn = StringUtils.isBlank(d[2]) ? null : d[2];
            } catch (ArrayIndexOutOfBoundsException e) {
                this.msisdn = null;
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EirsData eirsData = (EirsData) object;
        return Objects.equals(imei, eirsData.imei) && Objects.equals(imsi, eirsData.imsi) && Objects.equals(msisdn, eirsData.msisdn) && Objects.equals(tac, eirsData.tac);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imei, imsi, msisdn, tac);
    }
}
