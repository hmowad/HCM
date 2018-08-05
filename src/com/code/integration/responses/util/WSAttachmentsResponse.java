package com.code.integration.responses.util;

import com.code.integration.responses.WSResponseBase;

public class WSAttachmentsResponse extends WSResponseBase {

    private String attachmentId;
    private String url;

    public String getAttachmentId() {
	return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
	this.attachmentId = attachmentId;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

}
