package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSWFDefinitionLettersTypesResponse extends WSResponseBase {
    private List<WSWFDefinitionLetterTypeInfo> lettersTypes;

    @XmlElementWrapper(name = "lettersTypes")
    @XmlElement(name = "letterType")
    public List<WSWFDefinitionLetterTypeInfo> getLettersTypes() {
	return lettersTypes;
    }

    public void setLettersTypes(List<WSWFDefinitionLetterTypeInfo> lettersTypes) {
	this.lettersTypes = lettersTypes;
    }
}
