package com.code.ui.backings.hcm.promotions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.promotions.PromotionReportData;
import com.code.dal.orm.hcm.promotions.PromotionReportDetailData;
import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.dal.orm.workflow.hcm.promotions.WFPromotion;
import com.code.enums.CategoriesEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.hcm.PromotionsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.PromotionsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "promotionsSoldiersDecisionCancellation")
@ViewScoped
public class PromotionsSoldiersDecisionCancellation extends WFBaseBacking implements Serializable {

    private Long empId;
    private Long newJobId;
    private EmployeeData employee;
    private JobData empNewJob;

    private PromotionTransactionData promotionTransaction;
    private PromotionReportData promotionReportData;
    private PromotionReportDetailData promotionReportDetailData;
    private boolean isAdmin;
    private List<EmployeeData> internalCopies;

    public PromotionsSoldiersDecisionCancellation() throws BusinessException {
	if (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.PRM_SOLDIERS_CANCELLATION.getCode(), MenuActionsEnum.PRM_DECISION_SOLDIERS_CANCELLATION.getCode()))
	    isAdmin = true;
	employee = new EmployeeData();
	promotionTransaction = new PromotionTransactionData();
	this.init();
    }

    public void resetForm() {
	employee = null;
	promotionTransaction = null;
	empNewJob = null;
    }

    public void getEmployeeData() {
	try {
	    resetForm();
	    employee = EmployeesService.getEmployeeData(empId);
	    List<PromotionTransactionData> promotionTransactions = PromotionsService.getPromotionTransactionDataByEmpID(empId);
	    if (promotionTransactions.isEmpty())
		throw new BusinessException("error_soldierNotPromoted");
	    promotionTransaction = promotionTransactions.get(0);
	    if (employee != null) {
		promotionReportDetailData = PromotionsService.constructEmployeeReportDetail(empId, promotionReportData, promotionTransactions);
		promotionReportData.setRankId(promotionReportDetailData.getOldRankId());
		promotionReportData.setDueDate(employee.getPromotionDueDate());
		promotionReportData.setScaleUpFlagBoolean(false);
		promotionReportData.setPromotionDate(promotionTransaction.getOldLastPromotionDate());
	    }
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    resetForm();
	}
    }

    public Boolean changeEmpJob() {
	try {
	    if (((JobsService.getJobById(promotionTransaction.getOldJobId()).getRegionId() != employee.getPhysicalRegionId())
		    || JobsService.getJobById(promotionTransaction.getOldJobId()).getStatus() != JobStatusEnum.VACANT.getCode())
		    && empNewJob == null)
		return true;
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
	return false;
    }

    public void getEmpNewJopData() {
	try {
	    empNewJob = JobsService.getJobById(newJobId);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	    e.printStackTrace();
	}
    }

    public void init() {
	super.init();
	try {
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		WFPromotion wfPromotion = PromotionsWorkFlow.getWFPromotionByInstanceId(instance.getInstanceId());
		promotionReportData = PromotionsService.getPromotionReportDataById(wfPromotion.getReportId());
		promotionReportDetailData = PromotionsService.getPromotionReportDetailsDataByReportId(promotionReportData.getId()).get(0);
		employee = EmployeesService.getEmployeeData(promotionReportDetailData.getEmpId());
		internalCopies = EmployeesService.getEmployeesByIdsString(promotionReportData.getInternalCopies());
		promotionTransaction = PromotionsService.getPromotionTransactionById(promotionReportDetailData.getBasedOnTransactionId());
		empNewJob = JobsService.getJobById(promotionReportDetailData.getNewJobId());
	    } else {
		this.processId = WFProcessesEnum.SOLDIERS_PROMOTION_CANCELLATION.getCode();

		promotionReportData = new PromotionReportData();
		promotionReportData.setReportDate(HijriDateService.getHijriSysDate());
		promotionReportData.setCategoryId(CategoriesEnum.SOLDIERS.getCode());
		promotionReportData.setPromotionTypeId(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode());
		promotionReportData.setScaleUpFlagBoolean(false);
		promotionReportData.setRankId(null);
		internalCopies = new ArrayList<EmployeeData>();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	    e.printStackTrace();
	}
    }

    public String initProcess() throws BusinessException {
	try {
	    if (empNewJob == null) {
		empNewJob = JobsService.getJobById(promotionTransaction.getOldJobId());
	    }
	    promotionReportDetailData.setNewJobId(empNewJob.getId());
	    promotionReportDetailData.setNewJobCode(empNewJob.getCode());
	    promotionReportDetailData.setNewJobDesc(empNewJob.getName());
	    promotionReportDetailData.setNewJobClassDesc(empNewJob.getClassificationJobDescription());
	    promotionReportDetailData.setNewJobClassCode(empNewJob.getClassificationJobCode());

	    List<PromotionReportDetailData> promotionReportDetailsList = new ArrayList<PromotionReportDetailData>();
	    promotionReportDetailsList.add(promotionReportDetailData);
	    PromotionsWorkFlow.initPromotion(requester, promotionReportData, promotionReportDetailsList, processId, taskUrl, internalCopies);
	    return NavigationEnum.OUTBOX.toString();

	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String aproveSM() {
	try {
	    PromotionsWorkFlow.doPromotionSM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.APPROVE.getCode());
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

    public String rejectSM() {
	try {
	    PromotionsWorkFlow.doPromotionSM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveRE() {
	try {
	    List<PromotionReportDetailData> promotionReportDetailsList = new ArrayList<PromotionReportDetailData>();
	    promotionReportDetailsList.add(promotionReportDetailData);
	    PromotionsWorkFlow.doPromotionRE(requester, promotionReportData, promotionReportDetailsList, instance, currentTask, internalCopies, true);
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
		byte[] bytes = PromotionsService.getPromotionBytes(promotionTransaction);
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

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public Long getNewJobId() {
	return newJobId;
    }

    public void setNewJobId(Long newJobId) {
	this.newJobId = newJobId;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public PromotionTransactionData getPromotionTransaction() {
	return promotionTransaction;
    }

    public void setPromotionTransaction(PromotionTransactionData promotionTransaction) {
	this.promotionTransaction = promotionTransaction;
    }

    public JobData getEmpNewJob() {
	return empNewJob;
    }

    public void setEmpNewJob(JobData empNewJob) {
	this.empNewJob = empNewJob;
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

    public boolean isAdmin() {
	return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
	this.isAdmin = isAdmin;
    }

}
