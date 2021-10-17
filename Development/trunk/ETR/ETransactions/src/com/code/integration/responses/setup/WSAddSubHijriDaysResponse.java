package com.code.integration.responses.setup;

import com.code.integration.responses.WSResponseBase;

public class WSAddSubHijriDaysResponse extends WSResponseBase {

    private String hijriDateString;

    public String getHijriDateString() {
	return hijriDateString;
    }

    public void setHijriDateString(String hijriDateString) {
	this.hijriDateString = hijriDateString;
    }

}
