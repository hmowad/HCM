package com.code.ui.components.util;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.AttachmentsService;

@FacesComponent("com.code.ui.components.util.UIAttachments")
public class UIAttachments extends UINamingContainer {

    private String securityKey;

    public void calculateAttachmentsId() {
	try {
	    if (getAttributes().get("attachmentId") == null)
		setAttachmentsId(AttachmentsService.getNextAttachmentsId());
	    calculateAttachmentsSecurityKey();
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    public void calculateAttachmentsSecurityKey() {
	try {
	    securityKey = AttachmentsService.getSecurityKey();
	} catch (BusinessException e) {
	    e.printStackTrace();
	    securityKey = "";
	}
    }

    public String getAttachmentsAddURL() {
	if (getAttributes().get("attachmentId") != null && securityKey != null)
	    return ETRConfigurationService.getAttachmentsAddURL().replace("P_ATTACHMENTS_ID", (String) getAttributes().get("attachmentId")).replace("P_LOGGED_USER", (Long) getAttributes().get("loginEmpId") + "").replace("P_SEC_FLG", securityKey);
	else
	    return "";
    }

    public String getAttachmentsViewURL() {
	if ((getAttributes().get("attachmentId") != null) && (securityKey != null)) {
	    int accessRight = 0;
	    boolean viewEnableFlag = (Boolean) getAttributes().get("viewEnableFlag");
	    boolean deleteEnableFlag = (Boolean) getAttributes().get("deleteEnableFlag");
	    if (viewEnableFlag && deleteEnableFlag)
		accessRight = 3;
	    else if (viewEnableFlag)
		accessRight = 1;
	    else if (deleteEnableFlag)
		accessRight = 2;

	    return ETRConfigurationService.getAttachmentsViewURL().replace("P_ATTACHMENTS_ID", (String) getAttributes().get("attachmentId")).replace("P_USER_NAME", (Long) getAttributes().get("loginEmpId") + "").replace("P_ACL_RIGHT", accessRight + "").replace("P_SEC_FLG", securityKey);
	} else
	    return "";
    }

    public boolean getAttachmentsIntegrationEnabled() {
	return ETRConfigurationService.getAttachmentsAddURL() != null && !ETRConfigurationService.getAttachmentsAddURL().isEmpty() ? true : false;
    }

    private void setAttachmentsId(String attachmentId) {
	getValueExpression("attachmentId").setValue(FacesContext.getCurrentInstance().getELContext(), attachmentId);
    }
}