package com.code.enums.eservices;

public enum WFTaskURLsEnum {

    NOTIFICATION(100L, "", "/WorkList/NotifyUser.jsf"),
    EXTENSION_REQUEST(1720L, "/Retirements/ExtensionRequest.jsf", "/EServices/Transactions/ExtensionRequest.jsf"),
    REEXTENSION_REQUEST(1730L, "/Retirements/ReExtensionRequest.jsf", "/EServices/Transactions/ReExtensionRequest.jsf"),
    ;

    private Long code;
    private String hcmURL;
    private String eservicesURL;

    WFTaskURLsEnum(Long code, String hcmURL, String eservicesURL) {
	this.code = code;
	this.hcmURL = hcmURL;
	this.eservicesURL = eservicesURL;
    }

    public Long getCode() {
	return code;
    }

    public String getHcmUrl() {
	return hcmURL;
    }
    
    public String getEservicesUrl() {
	return eservicesURL;
    }
}
