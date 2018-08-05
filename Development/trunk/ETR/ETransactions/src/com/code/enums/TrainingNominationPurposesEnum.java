package com.code.enums;

public enum TrainingNominationPurposesEnum {

    PREPARING_FOR_ACUIRING_VACANT_JOB(1),
    PREPARING_FOR_ACUIRING_OCCUPIED_CONTRACTOR_JOB(2),
    FOR_TRAINING_PURPOSES(3),
    PREPARING_FOR_NEW_TECHNIQUE_OR_MACHINE(4),
    INCREASING_KNOWLEDGE_OR_SKILLS(5);

    private int code;

    private TrainingNominationPurposesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
