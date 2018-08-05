package com.code.enums;

public enum PromotionCandidateStatusEnum {
    NON_CANDIDATE(5),
    CANDIDATE(10),
    CANDIDATE_SEQUENTIALLY(15),
    PRECEDENTED_POINTS(25),
    PROMOTED(30),
    ROYAL_OREDER_ISSUED(35);

    private long code;

    private PromotionCandidateStatusEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
