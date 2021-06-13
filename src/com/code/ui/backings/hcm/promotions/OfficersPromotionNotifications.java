package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.enums.NavigationEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.PromotionsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "officersPromotionNotifications")
@ViewScoped
public class OfficersPromotionNotifications extends WFBaseBacking {

    private String royalOrderNumber;
    private Date royalOrderDate;
    private String royalOrderDateString;

    private List<PromotionTransactionData> promotionTransactions;

    private final int pageSize = 10;

    public OfficersPromotionNotifications() {
	init();
    }

    public void init() {
	super.init();
	try {
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		promotionTransactions = PromotionsService.getEligibleOfficersPromotionTransactionsByInstanceId(instance.getInstanceId(), loginEmpData, currentTask.getFlexField1());
		if (promotionTransactions != null && promotionTransactions.size() > 0) {
		    royalOrderDate = promotionTransactions.get(0).getExternalDecisionDate();
		    royalOrderDateString = HijriDateService.getHijriDateString(royalOrderDate);
		    royalOrderNumber = promotionTransactions.get(0).getExternalDecisionNumber();
		}

	    } else {
		this.processId = WFProcessesEnum.OFFICERS_PROMOTION.getCode();
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
	    promotionTransactions = new ArrayList<PromotionTransactionData>();
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void search() {
	try {
	    promotionTransactions = PromotionsService.getUnnotifiedOfficersPromotionTransactionsByRoyalNumberAndRoyalDate(royalOrderNumber, royalOrderDate);
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deletePromotionTransaction(PromotionTransactionData promotionTransactionData) {
	promotionTransactions.remove(promotionTransactionData);
	if (promotionTransactions == null || promotionTransactions.size() == 0)
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
    }

    public String sendNotifications() {
	try {
	    PromotionsWorkFlow.sendOfficersPromotionNotifications(requester.getEmpId(), promotionTransactions, processId, taskUrl);
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

    public List<PromotionTransactionData> getPromotionTransactions() {
	return promotionTransactions;
    }

    public void setPromotionTransactions(List<PromotionTransactionData> promotionTransactions) {
	this.promotionTransactions = promotionTransactions;
    }

    public int getPageSize() {
	return pageSize;
    }

}
