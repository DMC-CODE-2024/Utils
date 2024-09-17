package com.eirs.pairs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class FileDataDto {

    private LocalDateTime date;
    private String imei;
    private String imsie;
    private String actualImei;
    private String msisdn;

    private String operatorName;
    private String filename;

    private String operator;

    private boolean isGsmaValid;

    private boolean isCustomPaid; //3=amnisty and allowed, 0 invalid other than zero is valid.

    public FileDataDto(String str, DateTimeFormatter dateTimeFormatter) {
        String data[] = str.split(",");
        this.imei = data[0];
        this.imsie = data[1];
        this.date = LocalDateTime.parse(data[2], dateTimeFormatter);
        this.filename = data[3];

    }

}
