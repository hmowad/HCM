package com.code.enums;

public enum PromotionsTypesEnum {

    NORMAL_PROMOTION(1),
    EXCEPTIONAL_PROMOTION(2),
    GRANTING_SENIORITY(3),
    REVOKING_SENIORITY(4),
    RANK_REDUCE(5),
    PROMOTION_CANCELLATION(6);

    private long code;

    private PromotionsTypesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}