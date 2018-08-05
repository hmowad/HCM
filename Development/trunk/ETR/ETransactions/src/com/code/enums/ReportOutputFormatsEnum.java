package com.code.enums;

public enum ReportOutputFormatsEnum {

    PDF("PDF"),
    RTF("RTF"),
    DOCX("DOCX");
    
    private String code;
    
    private ReportOutputFormatsEnum(String code){
        this.code = code;
    }
    
    public String getCode(){return code;}
}
