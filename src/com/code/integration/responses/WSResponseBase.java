package com.code.integration.responses;

import java.io.Serializable;

import com.code.enums.WSResponseStatusEnum;

public class WSResponseBase implements Serializable {

    private int status;
    private String message;

    public WSResponseBase() {
	status = WSResponseStatusEnum.SUCCESS.getCode();
	message = "";
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

}
