package com.code.integration.responses.hcm;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WSWFDefinitionLetterTypeInfo implements Serializable {

    private String letterType;
    private String letterTypeDesc;

    public WSWFDefinitionLetterTypeInfo() {

    }

    public WSWFDefinitionLetterTypeInfo(String letterType, String letterTypeDesc) {
	this.letterType = letterType;
	this.letterTypeDesc = letterTypeDesc;
    }

    public String getLetterType() {
	return letterType;
    }

    public void setLetterType(String letterType) {
	this.letterType = letterType;
    }

    public String getLetterTypeDesc() {
	return letterTypeDesc;
    }

    public void setLetterTypeDesc(String letterTypeDesc) {
	this.letterTypeDesc = letterTypeDesc;
    }

}
