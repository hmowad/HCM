package com.code.integration.responses.workflow.generalnotifications;

import com.code.integration.responses.WSResponseBase;

public class WSGeneralMessageResponse extends WSResponseBase {

    private String generalMessage;

    public String getGeneralMessage() {
	return generalMessage;
    }

    public void setGeneralMessage(String generalMessage) {
	this.generalMessage = generalMessage;
    }

}
