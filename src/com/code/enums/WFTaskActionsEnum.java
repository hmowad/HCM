package com.code.enums;

public enum WFTaskActionsEnum {
    APPROVE("\u0645\u0640\u0640\u0648\u0627\u0641\u0642\u0640\u0640\u0629"),
    REJECT("\u0631\u0641\u0640\u0640\u0636"),
    RETURN_REVIEWER("\u0627\u0631\u062C\u0627\u0639 \u0627\u0644\u0645\u062F\u0642\u0642"),
    REDIRECT("\u062A\u0648\u062C\u064A\u0647"),
    REDIRECT_SECURITY("\u062a\u0648\u062c\u064a\u0647 \u0644\u0644\u0623\u0645\u0646 \u0648\u0627\u0644\u0625\u0633\u062a\u062e\u0628\u0627\u0631\u0627\u062a"),
    SUPER_SIGN("\u0625\u0639\u062A\u0645\u0627\u062F"),
    SUBMIT_TO_STUDY("\u0645\u0648\u0627\u0641\u0642\u0629 \u0644\u0644\u0625\u062D\u0627\u0644\u0629 \u0644\u0644\u062F\u0631\u0627\u0633\u0629"),
    REVIEW("\u062A\u062F\u0642\u064A\u0642"),
    DATA_ENTRY("\u062A\u0633\u062C\u064A\u0644 \u0628\u064A\u0627\u0646\u0627\u062A"),
    NOTIFIED("\u062A\u0640\u0640\u0640\u0645 \u0627\u0644\u0625\u0637\u0640\u0640\u0640\u0644\u0627\u0639"),
    PREPARE("\u0627\u0639\u062F\u0627\u062F"),
    RECOMMEND_APPROVE("\u0627\u0648\u0635\u064A \u0628\u0627\u0644\u0625\u0639\u062A\u0645\u0627\u062F"),
    RECOMMEND_REVIEW("\u062A\u062D\u062A\u0627\u062C \u0644\u062A\u062F\u0642\u064A\u0642"),
    RETURN_TO_COMMITTEE_PRESIDENT("\u0627\u0631\u062C\u0627\u0639 \u0644\u0631\u0626\u064A\u0633 \u0627\u0644\u0644\u062C\u0646\u0629"),
    RETURN_TO_COMMITTEE_SECRETARY("\u0627\u0631\u062C\u0627\u0639 \u0644\u0644\u0633\u0643\u0631\u062A\u064A\u0631"),
    RETURN_TO_PREPARATOR("\u0627\u0631\u062C\u0627\u0639 \u0644\u0644\u0645\u0639\u062F"),
    SEND_FOR_ADVISE("\u0627\u0631\u0633\u0627\u0644 \u0644\u0625\u0628\u062F\u0627\u0621 \u0627\u0644\u0631\u0623\u064A"),
    CLAIM("\u0645\u0637\u0627\u0644\u0628"),
    DISCLAIM("\u063A\u064A\u0631 \u0645\u0637\u0627\u0644\u0628"),
    SEND_BACK_TO_UNITS("\u0625\u0631\u062C\u0627\u0639 \u0625\u0644\u0649 \u0627\u0644\u062C\u0647\u0629");

    private String code;

    private WFTaskActionsEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
