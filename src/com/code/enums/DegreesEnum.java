package com.code.enums;

public enum DegreesEnum {

    FIRST(1),
    SECOND(2),
    THIRD(3),
    FORTH(4),
    FIFTH(5),
    SIXTH(6),
    SEVENTH(7),
    EIGHTH(8),
    NINTH(9),
    TENTH(10),
    ELEVENTH(11),
    TWELFTH(12),
    THIRTEENTH(13),
    FOURTEENTH(14),
    FIFTEENTH(15);

    private long code;

    private DegreesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
