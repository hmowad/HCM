package com.code.ui.backings.generalnotifications;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.NavigationEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.generalnotifications.GeneralNotificationsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "generalMessages")
@ViewScoped
public class GeneralMessages extends WFBaseBacking {

    private String message;

    public GeneralMessages() {
	init();
	message = this.currentTask.getFlexField1();
    }

    public String closeProcess() {
	try {
	    GeneralNotificationsWorkFlow.closeWFInstanceByNotification(this.instance, this.currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String getMessage() {
	return message;
    }

}
