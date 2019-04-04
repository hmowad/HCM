package com.code.enums;

public enum ReportOutputFormatsEnum {

    PDF("PDF"),
    RTF("RTF"),
    DOCX("DOCX"),
    XLS("XLS");

    private String code;

    private ReportOutputFormatsEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
