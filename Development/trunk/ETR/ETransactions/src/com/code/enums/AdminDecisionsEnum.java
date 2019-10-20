package com.code.enums;

public enum AdminDecisionsEnum {

    OFFICERS_DISCLAIMER("\u0625\u062c\u0631\u0627\u0621 \u0625\u062e\u0644\u0627\u0621 \u0637\u0631\u0641 \u0644\u0644\u0636\u0628\u0627\u0637"),
    SOLDIERS_DISCLAIMER("\u0625\u062c\u0631\u0627\u0621 \u0625\u062e\u0644\u0627\u0621 \u0637\u0631\u0641 \u0644\u0644\u0623\u0641\u0631\u0627\u062f"),
    OFFICERS_RECRUITMENT("\u062a\u0639\u064a\u064a\u0646 \u0627\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_REGISTRATION("\u062a\u0633\u062c\u064a\u0644 \u0636\u0627\u0628\u0637 \u062c\u062f\u064a\u062f");

    private String code;

    private AdminDecisionsEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
