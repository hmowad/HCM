package com.code.integration.responses.workflow.generalnotifications;

import com.code.integration.responses.WSResponseBase;

public class WSNotificationMessageResponse extends WSResponseBase {

    private String notificationMessage;

    public String getNotificationMessage() {
	return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
	this.notificationMessage = notificationMessage;
    }

}
