package com.code.enums.eservices;

public enum StoppingCriteriaEnum {
    PRESIDENCY(1, "\u0627\u0644\u0645\u062F\u064A\u0631 \u0627\u0644\u0639\u0627\u0645"), // moder 3am
    REGION_COMMANDER(2, "\u0642\u0627\u0626\u062F \u0627\u0644\u0645\u0646\u0637\u0642\u0629"), // ka2d el mantka
    ASSISTANT_PRESIDENCY(3, "\u0645\u062F\u064A\u0631 \u0645\u0631\u062A\u0628\u0637 \u0628\u0627\u0644\u0645\u062F\u064A\u0631 \u0627\u0644\u0639\u0627\u0645"), // moder mortbt blmoder el 3am
    ASSISTANT_REGION_COMMANDER(4, "\u0645\u062F\u064A\u0631 \u0645\u0631\u062A\u0628\u0637 \u0628\u0642\u0627\u0626\u062F \u0627\u0644\u0645\u0646\u0637\u0642\u0629"); // moder mortabt b ka2d el mantka

    private int code;
    private String codeDescription;

    StoppingCriteriaEnum(int code, String codeDescription) {
	this.code = code;
	this.codeDescription = codeDescription;
    }

    public int getCode() {
	return code;
    }

    public String getCodeDescription() {
	return codeDescription;
    }

    public static String valueOfNumber(Long number) {
	for (StoppingCriteriaEnum e : values()) {
	    if (e.code == number) {
		return e.codeDescription;
	    }
	}
	return null;
    }

}

