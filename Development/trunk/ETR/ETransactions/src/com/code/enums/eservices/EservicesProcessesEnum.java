package com.code.enums.eservices;

public enum EservicesProcessesEnum {
    EXTENSION_REQUEST(1720L,100L),
    REEXTENSION_REQUEST(1730L,100L);

    private long code;
    private long group;

    private EservicesProcessesEnum(Long code, Long group) {
	this.code = code;
	this.group = group;
    }

    public long getCode() {
	return code;
    }

    public long getGroup() {
	return group;
    }
}
