package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PromotionsService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "promotionsInquiry")
@ViewScoped
public class PromotionsInquiry extends BaseBacking {

    private int mode;
    private int adminUser;
    private List<EmployeeDecisionPrivilege> decisionsPrivileges;

    // For Search
    private Long empId;
    private String empName;
    private String decisionNumber;
    private Long promotionType;
    private List<PromotionTransactionData> promotionTransactionDataList;

    public PromotionsInquiry() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		if (mode == CategoryModesEnum.SOLDIERS.getCode())
		    setScreenTitle(getMessage("title_promotionsInquirySoldiers"));
		else if (mode == CategoryModesEnum.CIVILIANS.getCode())
		    setScreenTitle(getMessage("title_promotionsInquiryEmployees"));
		else
		    setServerSideErrorMessages(getMessage("error_general"));
	    } else {
		setServerSideErrorMessages(getMessage("error_general"));
	    }

	    decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.PROMOTIONS.getCode(), mode);
	    adminUser = decisionsPrivileges.size() == 0 ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode();
	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	empId = null;
	empName = null;
	decisionNumber = null;
	promotionTransactionDataList = new ArrayList<PromotionTransactionData>();
	promotionType = PromotionsTypesEnum.NORMAL_PROMOTION.getCode();
    }

    public void searchPromotions() {
	try {

	    if (!SecurityService.isDecisionPrivilegeGranted(this.loginEmpData, empId, decisionsPrivileges))
		throw new BusinessException("error_notAuthorized");

	    promotionTransactionDataList = PromotionsService.getPromotionTransactionsDecisions(decisionNumber, null, empId == null ? FlagsEnum.ALL.getCode() : empId, mode, promotionType);
	} catch (BusinessException e) {
	    promotionTransactionDataList = new ArrayList<PromotionTransactionData>();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    e.printStackTrace();
	    promotionTransactionDataList = new ArrayList<PromotionTransactionData>();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void printPromotion(PromotionTransactionData promotion) {
	try {
	    byte[] bytes = PromotionsService.getPromotionBytes(promotion);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Long getPromotionType() {
	return promotionType;
    }

    public void setPromotionType(Long promotionType) {
	this.promotionType = promotionType;
    }

    public List<PromotionTransactionData> getPromotionTransactionDataList() {
	return promotionTransactionDataList;
    }

    public void setPromotionTransactionDataList(List<PromotionTransactionData> promotionTransactionDataList) {
	this.promotionTransactionDataList = promotionTransactionDataList;
    }

    public int getAdminUser() {
	return adminUser;
    }

    public void setAdminUser(int adminUser) {
	this.adminUser = adminUser;
    }

    public long getEmployeesSearchRegionId() {
	return getLoginEmpPhysicalRegionFlag(adminUser == 0 ? false : true);
    }
}
