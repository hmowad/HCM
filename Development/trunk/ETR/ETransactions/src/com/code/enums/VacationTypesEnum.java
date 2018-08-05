package com.code.enums;

public enum VacationTypesEnum {
    REGULAR(1),
    SICK(2),
    EXCEPTIONAL(3),
    COMPELLING(5),
    ACCOMPANY(9),
    FIELD(11),

    DEATH_WAITING_PERIOD(13),
    MATERNITY(14),
    MOTHERHOOD(15),
    ORPHAN_CARE(16),

    DEATH_OF_RELATIVE(17),
    NEW_BABY(18),

    STUDY(19),
    EXAM(20),

    SPORTIVE(12),
    CULTURAL(21),
    SOCIAL(22),

    RELIEF(23),

    COMPENSATION(24);

    private long code;

    private VacationTypesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
