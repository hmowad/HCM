package com.code.enums;

public enum SequenceNamesEnum {

    ETR_ATTACHMENTS_SEQUENCE("ETR_ATTACHMENTS_SEQ"),
    SOLDIERS_SEQUENCE_NUMBER("HCM_SOLDIERS_NUMBER_SEQ");

    private String code;

    private SequenceNamesEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
