package com.code.integration.responses.util;

import com.code.integration.responses.WSResponseBase;

public class WSPrintResponse extends WSResponseBase {

    private byte[] printBytes;

    public byte[] getPrintBytes() {
	return printBytes;
    }

    public void setPrintBytes(byte[] printBytes) {
	this.printBytes = printBytes;
    }

}
