package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.promotions.PromotionReportData;
import com.code.dal.orm.hcm.promotions.PromotionReportDetailData;
import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.dal.orm.workflow.hcm.promotions.WFPromotion;
import com.code.enums.CategoriesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.PromotionsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "soldiersExceptionalPromotion")
@ViewScoped
public class SoldiersExceptionalPromotion extends WFBaseBacking {

    private Long selectedEmployeeId;
    private EmployeeData selectedEmployee;
    private PromotionReportData promotionReportData;
    private PromotionReportDetailData promotionReportDetailData;

    private List<EmployeeData> internalCopies;
    private List<PromotionReportDetailData> promotionReportDetailsList;

    public SoldiersExceptionalPromotion() {
	this.init();
    }

    public void init() {
	super.init();
	try {
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		WFPromotion wfPromotion = PromotionsWorkFlow.getWFPromotionByInstanceId(instance.getInstanceId());
		promotionReportData = PromotionsService.getPromotionReportDataById(wfPromotion.getReportId());
		promotionReportDetailsList = PromotionsService.getPromotionReportDetailsDataByReportId(promotionReportData.getId());
		promotionReportDetailData = promotionReportDetailsList.get(0);
		selectedEmployee = EmployeesService.getEmployeeData(promotionReportDetailData.getEmpId());
		internalCopies = EmployeesService.getEmployeesByIdsString(promotionReportData.getInternalCopies());
	    } else {
		this.processId = WFProcessesEnum.SOLDIERS_EXCEPTIONAL_PROMOTION.getCode();

		promotionReportData = new PromotionReportData();
		promotionReportData.setReportDate(HijriDateService.getHijriSysDate());
		promotionReportData.setCategoryId(CategoriesEnum.SOLDIERS.getCode());
		promotionReportData.setPromotionTypeId(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode());
		promotionReportData.setScaleUpFlagBoolean(false);
		promotionReportData.setRankId(null);
		promotionReportDetailsList = new ArrayList<PromotionReportDetailData>();
		internalCopies = new ArrayList<EmployeeData>();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // used only in this class
    private void reset() {
	selectedEmployeeId = null;
	selectedEmployee = null;
	promotionReportData.setRankId(null);
	promotionReportDetailData = null;
	promotionReportDetailsList = new ArrayList<PromotionReportDetailData>();
    }

    public void searchSelectedSoldier() {
	try {
	    if (selectedEmployeeId != null) {
		selectedEmployee = EmployeesService.getEmployeeData(selectedEmployeeId);
		promotionReportDetailData = PromotionsService.constructEmployeeReportDetail(selectedEmployeeId, promotionReportData, null);
		promotionReportDetailsList = new ArrayList<PromotionReportDetailData>();
		promotionReportDetailsList.add(promotionReportDetailData);
		promotionReportData.setRankId(promotionReportDetailData.getOldRankId());
		promotionReportData.setDueDate(selectedEmployee.getPromotionDueDate());
		promotionReportData.setScaleUpFlagBoolean(false);
	    }
	} catch (BusinessException e) {
	    reset();
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void scaleUpJob() {
	try {
	    PromotionsService.handleScaleUpJob(promotionReportDetailData, promotionReportData.getScaleUpFlagBoolean());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String initProcess() {
	try {
	    PromotionsWorkFlow.initPromotion(requester, promotionReportData, promotionReportDetailsList, processId, taskUrl, internalCopies);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approvePDM() {
	try {
	    PromotionsWorkFlow.doPromotionPDM(requester, instance, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifyPDM() {
	try {
	    PromotionsWorkFlow.doPromotionPDM(requester, instance, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveAOM() {
	try {
	    PromotionsWorkFlow.doPromotionAOM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifyAOM() {
	try {
	    PromotionsWorkFlow.doPromotionAOM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveRE() {
	try {
	    PromotionsWorkFlow.doPromotionRE(requester, promotionReportData, promotionReportDetailsList, instance, currentTask, internalCopies, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectRE() {
	try {
	    PromotionsWorkFlow.doPromotionRE(requester, promotionReportData, null, instance, currentTask, internalCopies, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String signSM() {
	try {
	    PromotionsWorkFlow.doPromotionSM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectSM() {
	try {
	    PromotionsWorkFlow.doPromotionSM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifySM() {
	try {
	    PromotionsWorkFlow.doPromotionSM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public void print() {
	try {
	    PromotionTransactionData promotionTransaction = PromotionsService.getPromotionTransactionByDecisionNumberAndDecisionDate(promotionReportData.getDecisionNumber(), promotionReportData.getDecisionDate(), promotionReportData.getPromotionTypeId());
	    if (promotionTransaction != null) {
		byte[] bytes = PromotionsService.getPromotionBytes(promotionTransaction, null, null);
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
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

    public Long getSelectedEmployeeId() {
	return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(Long selectedEmployeeId) {
	this.selectedEmployeeId = selectedEmployeeId;
    }

    public EmployeeData getSelectedEmployee() {
	return selectedEmployee;
    }

    public PromotionReportData getPromotionReportData() {
	return promotionReportData;
    }

    public void setPromotionReportData(PromotionReportData promotionReportData) {
	this.promotionReportData = promotionReportData;
    }

    public PromotionReportDetailData getPromotionReportDetailData() {
	return promotionReportDetailData;
    }

    public void setPromotionReportDetailData(PromotionReportDetailData promotionReportDetailData) {
	this.promotionReportDetailData = promotionReportDetailData;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }
}
