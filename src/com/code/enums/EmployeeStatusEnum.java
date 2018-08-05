package com.code.enums;

public enum EmployeeStatusEnum {
    
    UNDER_RECRUITMENT(1),
    NEW_STUDENT_SOLDIER(5),
    NEW_STUDENT_RANKED_SOLDIER(10),
    ON_DUTY_UNDER_RECRUITMENT(12),
    ON_DUTY(15),
    SUBJOINED(20),
    PERSONS_SUBJOINED(22),
    ASSIGNED(25),
    MANDATED(30),
    SECONDMENTED(35),
    ASSIGNED_EXTERNALLY(40),
    PERSONS_SUBJOINED_EXTERNALLY(42),
    SUBJOINED_EXTERNALLY(45),
    MOVED_EXTERNALLY(50),
    SERVICE_TERMINATED(70),
    NOT_ACTIVE(99);

    private long code;

    private EmployeeStatusEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
