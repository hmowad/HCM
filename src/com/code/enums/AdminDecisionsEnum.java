package com.code.enums;

public enum AdminDecisionsEnum {

    /****************************************** Disclaimer ********************************************/
    OFFICERS_DISCLAIMER("\u0625\u062c\u0631\u0627\u0621 \u0625\u062e\u0644\u0627\u0621 \u0637\u0631\u0641 \u0644\u0644\u0636\u0628\u0627\u0637"),
    SOLDIERS_DISCLAIMER("\u0625\u062c\u0631\u0627\u0621 \u0625\u062e\u0644\u0627\u0621 \u0637\u0631\u0641 \u0644\u0644\u0623\u0641\u0631\u0627\u062f"),

    /****************************************** Recruitment ********************************************/
    OFFICERS_RECRUITMENT("\u062a\u0639\u064a\u064a\u0646 \u0627\u0644\u0636\u0628\u0627\u0637"),

    /****************************************** Employees ********************************************/
    OFFICERS_REGISTRATION("\u062a\u0633\u062c\u064a\u0644 \u0636\u0627\u0628\u0637 \u062c\u062f\u064a\u062f"),

    /****************************************** Vacations ********************************************/
    OFFICERS_REGULAR_VACATION_REQUEST("\u0637\u0644\u0628 \u0625\u062c\u0627\u0632\u0629 \u0625\u0639\u062a\u064a\u0627\u062f\u064a\u0629 \u0644\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_COMPELLING_VACATION_REQUEST("\u0637\u0644\u0628 \u0625\u062c\u0627\u0632\u0629 \u0639\u0631\u0636\u064a\u0629 \u0644\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_SICK_VACATION_REQUEST("\u0637\u0644\u0628 \u0625\u062c\u0627\u0632\u0629 \u0645\u0631\u0636\u064a\u0629 \u0644\u0644\u0636\u0628\u0627\u0637"),

    /****************************************** Termination ********************************************/
    CIVILLIANS_TERMINATION_RECORD("\u0625\u062c\u0631\u0627\u0621\u0627\u062a \u0625\u0646\u0647\u0627\u0621 \u0627\u0644\u062e\u062f\u0645\u0629 \u002f \u0625\u0646\u0647\u0627\u0621 \u0627\u0644\u062a\u0639\u0627\u0642\u062f \u0644\u0644\u0645\u0648\u0638\u0641\u064a\u0646"),

    /****************************************** Movements ********************************************/
    OFFICERS_MOVE_DECISION_REQUEST("\u0625\u0635\u062f\u0627\u0631 \u0642\u0631\u0627\u0631 \u0646\u0642\u0644 \u0644\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_MOVE_REGISTERATION("\u062a\u0633\u062c\u064a\u0644 \u0642\u0631\u0627\u0631 \u0646\u0642\u0644 \u062e\u0627\u0631\u062c\u064a \u0644\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_SUBJOIN_DECISION_REQUEST("\u0625\u0635\u062f\u0627\u0631 \u0642\u0631\u0627\u0631 \u0625\u0644\u062d\u0627\u0642 \u0644\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_SUBJOIN_REGISTERATION("\u062a\u0633\u062c\u064a\u0644 \u0642\u0631\u0627\u0631 \u0625\u0644\u062d\u0627\u0642 \u062e\u0627\u0631\u062c\u064a \u0644\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_INTERNAL_ASSIGNMENT_DECISION_REQUEST("\u0625\u0635\u062f\u0627\u0631 \u0623\u0645\u0631 \u062a\u0643\u0644\u064a\u0641 \u0644\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_ASSIGNMENT_REGISTERATION("\u062a\u0633\u062c\u064a\u0644 \u0623\u0645\u0631 \u062a\u0643\u0644\u064a\u0641 \u062e\u0627\u0631\u062c\u064a \u0644\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_MOVE_JOINING_DATE_REQUEST("\u0637\u0644\u0628 \u0645\u0628\u0627\u0634\u0631\u0629 \u0646\u0642\u0644 \u0644\u0644\u0636\u0628\u0627\u0637"),
    OFFICERS_SUBJOIN_JOINING_DATE_REQUEST("\u0637\u0644\u0628 \u0645\u0628\u0627\u0634\u0631\u0629 \u0627\u0644\u062d\u0627\u0642 \u0644\u0644\u0636\u0628\u0627\u0637");

    private String code;

    private AdminDecisionsEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
