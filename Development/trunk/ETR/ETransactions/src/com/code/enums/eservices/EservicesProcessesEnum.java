package com.code.enums.eservices;

public enum EservicesProcessesEnum {
	EXTENSION_REQUEST(1720),
    REEXTENSION_REQUEST(1730);

    private long code;

    private EservicesProcessesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
