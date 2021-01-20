package com.code.ui.components.util;

import java.io.Serializable;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.code.integration.webservices.hcm.AttachmentServiceCallBack;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.EmployeeFileAttachmentService;
import com.code.services.util.EncryptionUtil;

@FacesComponent("com.code.ui.components.util.UIEmployeeFileAttachment")
public class UIEmployeeFileAttachment extends UINamingContainer implements Serializable {
    private String uploadURL;
    private String fileUploadParams;

    private String attachmentId;

    public UIEmployeeFileAttachment() {
	uploadURL = ETRConfigurationService.getAttachmentUploadURL();
    }

    private HttpServletRequest getRequest() {
	return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    /**
     * Prepare Upload parameters
     * 
     */
    public void getUploadParam(Long empFileAttachmentId) {
	try {
	    String requestURL = getRequest().getRequestURL() + "";
	    String url = requestURL.replace(getRequest().getServletPath(), "");
	    String serviceURL = url + AttachmentServiceCallBack.SERVICE_URL;
	    fileUploadParams = EncryptionUtil.encryptSymmetrically("code=" + EmployeeFileAttachmentService.SERVICE_CODE + "&token=" + empFileAttachmentId + "&serviceurl=" + serviceURL);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public String getUploadURL() {
	return uploadURL;
    }

    public void setUploadURL(String uploadURL) {
	this.uploadURL = uploadURL;
    }

    public String getFileUploadParams() {
	return fileUploadParams;
    }

    public void setFileUploadParams(String fileUploadParams) {
	this.fileUploadParams = fileUploadParams;
    }

    public String getAttachmentId() {
	return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
	this.attachmentId = attachmentId;
    }

}
