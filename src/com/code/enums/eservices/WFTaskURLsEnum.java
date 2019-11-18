package com.code.enums.eservices;

public enum WFTaskURLsEnum {

    NOTIFICATION(0L, "/EServices/WorkList/NotifyUser.jsf"),
    EXTENSION_REQUEST(1720L, "/EServices/Transactions/ExtensionRequest.jsf"),
    REEXTENSION_REQUEST(1730L, "/EServices/Transactions/ReExtensionRequest.jsf"),
    ;

    private Long code;
    private String url;

    WFTaskURLsEnum(Long code, String url) {
	this.code = code;
	this.url = url;
    }

    public Long getCode() {
	return code;
    }

    public String getUrl() {
	return url;
    }
}
