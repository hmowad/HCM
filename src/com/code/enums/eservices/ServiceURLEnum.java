package com.code.enums.eservices;

public enum ServiceURLEnum {
    NEXT_SEQUENCE_VALUE("/common/getNextSequenceValue"),
    ALL_WF_PROCESS_CYCLES("wfBase/wfProcessCycles"),
    UPDATE_WF_PROCESS_CYCLES("wfBase/updateWFProcessCycles")
    ;
    
    private String code;

    ServiceURLEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
