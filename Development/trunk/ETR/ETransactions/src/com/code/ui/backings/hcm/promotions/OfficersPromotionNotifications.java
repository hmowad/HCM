package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.workflow.hcm.promotions.WFPromotionNotificationData;
import com.code.enums.NavigationEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.PromotionsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "officersPromotionNotifications")
@ViewScoped
public class OfficersPromotionNotifications extends WFBaseBacking {

    private String royalOrderNumber;
    private Date royalOrderDate;
    private String royalOrderDateString;

    private List<WFPromotionNotificationData> wfPromotionNotifications;

    private final int pageSize = 10;

    public OfficersPromotionNotifications() {
	init();
    }

    public void init() {
	super.init();
	try {
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfPromotionNotifications = PromotionsWorkFlow.getWFPromotionNotifications(instance.getInstanceId(), loginEmpData, currentTask.getFlexField1());
		if (wfPromotionNotifications != null && wfPromotionNotifications.size() > 0) {
		    royalOrderDate = wfPromotionNotifications.get(0).getExternalDecisionDate();
		    royalOrderDateString = wfPromotionNotifications.get(0).getDueDateString();
		    royalOrderNumber = wfPromotionNotifications.get(0).getExternalDecisionNumber();
		}

	    } else {
		this.processId = WFProcessesEnum.OFFICERS_PROMOTION_NOTIFICATION.getCode();
		reset();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	try {
	    royalOrderNumber = "";
	    royalOrderDate = HijriDateService.getHijriSysDate();
	    wfPromotionNotifications = new ArrayList<WFPromotionNotificationData>();
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void search() {
	try {
	    wfPromotionNotifications = PromotionsWorkFlow.costructWFPromotionNotifications(royalOrderNumber, royalOrderDate);
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteWFPromotionNotificationData(WFPromotionNotificationData wfPromotionNotificationData) {
	wfPromotionNotifications.remove(wfPromotionNotificationData);
	if (wfPromotionNotifications == null || wfPromotionNotifications.size() == 0)
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
    }

    public String initProcess() {
	try {
	    PromotionsWorkFlow.initPromotionNotifications(requester.getEmpId(), wfPromotionNotifications, processId, taskUrl);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    PromotionsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String getRoyalOrderNumber() {
	return royalOrderNumber;
    }

    public void setRoyalOrderNumber(String royalOrderNumber) {
	this.royalOrderNumber = royalOrderNumber;
    }

    public Date getRoyalOrderDate() {
	return royalOrderDate;
    }

    public void setRoyalOrderDate(Date royalOrderDate) {
	this.royalOrderDate = royalOrderDate;
    }

    public String getRoyalOrderDateString() {
	return royalOrderDateString;
    }

    public void setRoyalOrderDateString(String royalOrderDateString) {
	this.royalOrderDateString = royalOrderDateString;
    }

    public List<WFPromotionNotificationData> getWfPromotionNotifications() {
	return wfPromotionNotifications;
    }

    public void setWfPromotionNotifications(List<WFPromotionNotificationData> wfPromotionNotifications) {
	this.wfPromotionNotifications = wfPromotionNotifications;
    }

    public int getPageSize() {
	return pageSize;
    }

}
