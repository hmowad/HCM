package com.code.enums.eservices;

public enum EServicesCategoryEnum {

    ALL(-1L, "\u0627\u0644\u0643\u0644"), // alkol
    OFFICERS(1L, "\u0627\u0644\u0636\u0628\u0627\u0637"), // zobat
    SOLDIERS(2L, "\u0627\u0644\u0623\u0641\u0631\u0627\u062F"), // alafrad
    PERSONS(3L, "\u0627\u0644\u062E\u062F\u0645\u0629 \u0627\u0644\u0645\u062F\u0646\u064A\u0629"), // al5edma almdnya
    USERS(4L, "\u0627\u0644\u0645\u0633\u062A\u062E\u062F\u0645\u064A\u0646"), // almost5dmen
    WAGES(5L, "\u0628\u0646\u062F \u0627\u0644\u0623\u062C\u0648\u0631"), // band elogor
    CONTRACTORS(6L, "\u0627\u0644\u0645\u062A\u0639\u0627\u0642\u062F\u064A\u0646"), // almota3akden
    MEDICAL_STAFF(9L, "\u0644\u0627\u0626\u062D\u0629 \u0627\u0644\u0635\u062D\u064A\u064A\u0646"); // la27t el s7yeen

    private Long code;
    private String codeDescription;

    EServicesCategoryEnum(Long code, String codeDescription) {
	this.code = code;
	this.codeDescription = codeDescription;
    }

    public Long getCode() {
	return code;
    }

    public String getCodeDescription() {
	return codeDescription;
    }

    public static String valueOfNumber(Long number) {
	for (EServicesCategoryEnum e : values()) {
	    if (e.code == number) {
		return e.codeDescription;
	    }
	}
	return null;
    }

}

