package com.code.enums;

public enum RegionsEnum {

    GENERAL_DIRECTORATE_OF_BORDER_GUARDS(1),
    EASTERN_REGION(2),
    NORTHERN_REGION(3),
    ALJOOF_REGION(4),
    TABOOK_REGION(6),
    MAKKAH_ALMOKARRAMAH_REGION(7),
    JAZAN_REGION(8),
    ASEER_REGION(9),
    NAJRAN_REGION(10),
    ALMADEENAH_ALMONAWRAH_REGION(12),
    ACADEMY(13);

    private long code;

    private RegionsEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}