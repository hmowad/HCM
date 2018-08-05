package com.code.integration.responses.security;

import com.code.integration.responses.WSResponseBase;

public class WSAuthenticationResponse extends WSResponseBase {

    private String sessionId;
    private long employeeId;

    public String getSessionId() {
	return sessionId;
    }

    public void setSessionId(String sessionId) {
	this.sessionId = sessionId;
    }

    public long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(long employeeId) {
	this.employeeId = employeeId;
    }

}
