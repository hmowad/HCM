package com.code.ui.backings.hcm.retirements;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.retirements.DisclaimerTransactionData;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.hcm.retirements.WFDisclaimerData;
import com.code.dal.orm.workflow.hcm.retirements.WFDisclaimerDetail;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.RetirementsService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.hcm.RetirementsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "disclaimerRequest")
@ViewScoped
public class DisclaimerRequest extends WFBaseBacking {

    private EmployeeData employee;
    private WFDisclaimerData wfDisclaimerData;
    private WFDisclaimerDetail wfDisclaimerDetail;

    private int mode;
    private boolean isAdmin;
    private boolean isManager;
    private boolean isPayrollReviewer;
    private long regionId;

    private Long reviewerEmpId;
    private String hkeyReviewerEmps;
    private String sentBackUnitsIdsParam;
    private String selectedSentBackUnitsNamesString;

    public DisclaimerRequest() {
	prevTasksNoLevelFlag = true;
	super.init();
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		employee = new EmployeeData();
		employee.setCategoryId(new Long(mode));
		wfDisclaimerData = new WFDisclaimerData();
		reviewerEmpId = null;
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersDisclaimerRequest"));
		    this.processId = WFProcessesEnum.OFFICERS_DISCLAIMER_REQUEST.getCode();
		    if (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.RET_OFFICIERS_DISCLAIMER_REQUEST.getCode(), MenuActionsEnum.RET_DISCLAIMER_REQUEST_CREATE.getCode()))
			isAdmin = true;
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersDisclaimerRequest"));
		    this.processId = WFProcessesEnum.SOLDIERS_DISCLAIMER_REQUEST.getCode();
		    if (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.RET_SOLDIERS_DISCLAIMER_REQUEST.getCode(), MenuActionsEnum.RET_DISCLAIMER_REQUEST_CREATE.getCode()))
			isAdmin = true;
		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_URLError"));
		    break;
		}

		if (isAdmin) {
		    regionId = getLoginEmpPhysicalRegionFlag(isAdmin);
		} else if (loginEmpData.getIsManager() == FlagsEnum.ON.getCode()) {
		    isManager = true;
		}

		this.init();
		hkeyReviewerEmps = FlagsEnum.ALL.getCode() + "";
		WFPosition position = null;
		UnitData payrollUnitData = null;
		if (this.role.equals(WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode())) {
		    position = RetirementsWorkFlow.getRegionPayrollUnitManager(wfDisclaimerData.getEmpPhysicalRegionId());
		    payrollUnitData = UnitsService.getUnitById(position.getUnitId());
		    isPayrollReviewer = payrollUnitData.getPhysicalManagerId().equals(loginEmpData.getEmpId());
		    hkeyReviewerEmps = this.loginEmpData.getPhysicalUnitHKey();
		} else if (this.role.equals(WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode())) {
		    position = RetirementsWorkFlow.getRegionPayrollUnitManager(wfDisclaimerData.getEmpPhysicalRegionId());
		    payrollUnitData = UnitsService.getUnitById(position.getUnitId());
		    if (this.currentTask.getFlexField3() == null)
			setServerSideErrorMessages(getMessage("error_transactionDataError"));
		    UnitData SmSsmUnitData = UnitsService.getUnitById(Long.parseLong(this.currentTask.getFlexField3()));
		    isPayrollReviewer = payrollUnitData.getId().equals(SmSsmUnitData.getId());
		    if (SmSsmUnitData != null) {
			hkeyReviewerEmps = SmSsmUnitData.gethKey();
		    }
		} else if (this.role.equals(WFTaskRolesEnum.SIGN_MANAGER.getCode())) {
		    if (RetirementsWorkFlow.isRegionOfficerDisclaimerRequest(wfDisclaimerData)) {
			position = RetirementsWorkFlow.getRegionPayrollUnitManager(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			payrollUnitData = UnitsService.getUnitById(position.getUnitId());
			isPayrollReviewer = payrollUnitData.getPhysicalManagerId().equals(loginEmpData.getEmpId());
		    } else {
			position = RetirementsWorkFlow.getRegionPayrollUnitManager(wfDisclaimerData.getEmpPhysicalRegionId());
			payrollUnitData = UnitsService.getUnitById(position.getUnitId());
			isPayrollReviewer = payrollUnitData.getPhysicalManagerId().equals(loginEmpData.getEmpId());
		    }
		    hkeyReviewerEmps = this.loginEmpData.getPhysicalUnitHKey();
		} else if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {
		    position = RetirementsWorkFlow.getRegionPayrollUnitManager(wfDisclaimerData.getEmpPhysicalRegionId());
		    payrollUnitData = UnitsService.getUnitById(position.getUnitId());
		    if (this.currentTask.getFlexField3() == null)
			setServerSideErrorMessages(getMessage("error_transactionDataError"));
		    UnitData SmSsmUnitData = UnitsService.getUnitById(Long.parseLong(this.currentTask.getFlexField3()));
		    isPayrollReviewer = payrollUnitData.getId().equals(SmSsmUnitData.getId());
		    if (SmSsmUnitData != null) {
			hkeyReviewerEmps = SmSsmUnitData.gethKey();
		    }
		} else if (this.role.equals(WFTaskRolesEnum.EXTRA_SIGN_MANAGER.getCode())) {
		    sentBackUnitsIdsParam = RetirementsWorkFlow.getManagersUnitsIdsString(wfDisclaimerData.getInstanceId(), wfDisclaimerData.getEmpPhysicalRegionId());
		}
	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void init() {
	try {
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		if (loginEmpData.getCategoryId().intValue() == mode) {
		    employee = EmployeesService.getEmployeeData(loginEmpData.getEmpId());
		    wfDisclaimerData = RetirementsWorkFlow.constructWFDisclaimerData(loginEmpData);
		}
	    } else {
		prevTasks = BaseWorkFlow.getWFInstanceCompletedTasksDataOrderedByLevelLength(currentTask.getInstanceId(), currentTask.getTaskId());
		wfDisclaimerData = RetirementsWorkFlow.getWFDisclaimerDataByInstanceIds(new Long[] { (long) instance.getInstanceId() }).get(0);
		updateAmounts();
		employee = EmployeesService.getEmployeeData(wfDisclaimerData.getEmpId());
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateAmounts() {
	BigDecimal totalDueAmount = new BigDecimal(((wfDisclaimerData.getBasicAmount() == null) ? 0 : (wfDisclaimerData.getBasicAmount())) + ((wfDisclaimerData.getAllowanceAmount() == null) ? 0D : (wfDisclaimerData.getAllowanceAmount())));
	wfDisclaimerData.setTotalDueAmount(totalDueAmount);
	BigDecimal totalGovernmentalDueAmount = new BigDecimal(((wfDisclaimerData.getRealEstateFundAmount() == null) ? 0 : (wfDisclaimerData.getRealEstateFundAmount())) + ((wfDisclaimerData.getCreditBankAmount() == null) ? 0D : (wfDisclaimerData.getCreditBankAmount())) + ((wfDisclaimerData.getOtherAmount() == null) ? 0 : (wfDisclaimerData.getOtherAmount())));
	wfDisclaimerData.setTotalGovernmentalDueAmount(totalGovernmentalDueAmount);
    }

    public void getEmployeeData() {
	try {
	    EmployeeData selectedEmployee = EmployeesService.getEmployeeData(employee.getEmpId());
	    if (selectedEmployee == null)
		throw new BusinessException("error_UIError");
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfDisclaimerData = RetirementsWorkFlow.constructWFDisclaimerData(selectedEmployee);
		if (wfDisclaimerData.getTerminationTransactionId() == null)
		    throw new BusinessException("error_steEmpShouldBeTerminated");

		if (isAdmin && regionId != FlagsEnum.ALL.getCode() && wfDisclaimerData.getEmpPhysicalRegionId() != regionId) {
		    throw new BusinessException("error_notAuthorized");
		}

		employee = selectedEmployee;
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String initProcess() throws BusinessException {
	try {
	    RetirementsWorkFlow.initDisclaimer(requester, processId, attachments, taskUrl, wfDisclaimerData);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String doRetirementClaimSM() throws BusinessException {
	try {
	    wfDisclaimerData.getWFDisclaimer().setSystemUser(loginEmpData.getEmpId() + ""); // For auditing.
	    wfDisclaimerDetail = RetirementsWorkFlow.getWFDisclaimerDetailsByManagerUnitIdAndInstanceId(EmployeesService.getEmployeeData(currentTask.getOriginalId()).getPhysicalUnitId(), instance.getInstanceId()).get(0);
	    wfDisclaimerDetail.setClaimedFlag(FlagsEnum.ON.getCode());
	    if (this.role.equals(WFTaskRolesEnum.SIGN_MANAGER.getCode()))
		RetirementsWorkFlow.doSM(requester, instance, wfDisclaimerData, wfDisclaimerDetail, currentTask, WFActionFlagsEnum.CLAIM.getCode(), null);
	    else if (this.role.equals(WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode()))
		RetirementsWorkFlow.doSSM(requester, instance, wfDisclaimerData, wfDisclaimerDetail, currentTask, WFActionFlagsEnum.CLAIM.getCode(), null);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String doRetirementDisclaimSM() throws BusinessException {
	try {
	    wfDisclaimerData.getWFDisclaimer().setSystemUser(loginEmpData.getEmpId() + ""); // For auditing.
	    wfDisclaimerDetail = RetirementsWorkFlow.getWFDisclaimerDetailsByManagerUnitIdAndInstanceId(EmployeesService.getEmployeeData(currentTask.getOriginalId()).getPhysicalUnitId(), instance.getInstanceId()).get(0);
	    wfDisclaimerDetail.setClaimedFlag(FlagsEnum.OFF.getCode());
	    if (this.role.equals(WFTaskRolesEnum.SIGN_MANAGER.getCode()))
		RetirementsWorkFlow.doSM(requester, instance, wfDisclaimerData, wfDisclaimerDetail, currentTask, WFActionFlagsEnum.DISCLAIM.getCode(), null);
	    else if (this.role.equals(WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode()))
		RetirementsWorkFlow.doSSM(requester, instance, wfDisclaimerData, wfDisclaimerDetail, currentTask, WFActionFlagsEnum.DISCLAIM.getCode(), null);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String doRetirementRedirectSM() throws BusinessException {
	try {
	    wfDisclaimerData.getWFDisclaimer().setSystemUser(loginEmpData.getEmpId() + ""); // For auditing.
	    if (this.role.equals(WFTaskRolesEnum.SIGN_MANAGER.getCode()))
		RetirementsWorkFlow.doSM(requester, instance, wfDisclaimerData, null, currentTask, WFActionFlagsEnum.REDIRECT.getCode(), reviewerEmpId);
	    else if (this.role.equals(WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode()))
		RetirementsWorkFlow.doSSM(requester, instance, wfDisclaimerData, null, currentTask, WFActionFlagsEnum.REDIRECT.getCode(), reviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String doRetirementsAprroveRE() throws BusinessException {
	try {
	    wfDisclaimerData.getWFDisclaimer().setSystemUser(loginEmpData.getEmpId() + ""); // For auditing.
	    if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()))
		RetirementsWorkFlow.doRE(requester, wfDisclaimerData, processId, currentTask, WFActionFlagsEnum.APPROVE.getCode(), null);
	    else if (this.role.equals(WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode()))
		RetirementsWorkFlow.doSRE(requester, wfDisclaimerData, processId, currentTask, WFActionFlagsEnum.APPROVE.getCode(), null);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String doRetirementsRedirectRE() throws BusinessException {
	try {
	    wfDisclaimerData.getWFDisclaimer().setSystemUser(loginEmpData.getEmpId() + ""); // For auditing.
	    if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()))
		RetirementsWorkFlow.doRE(requester, wfDisclaimerData, processId, currentTask, WFActionFlagsEnum.REDIRECT.getCode(), reviewerEmpId);
	    else if (this.role.equals(WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode()))
		RetirementsWorkFlow.doSRE(requester, wfDisclaimerData, processId, currentTask, WFActionFlagsEnum.REDIRECT.getCode(), reviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String doRetirementsAprroveESM() throws BusinessException {
	try {
	    RetirementsWorkFlow.doESM(requester, instance, wfDisclaimerData, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String doRetirementsRejectESM() throws BusinessException {
	try {
	    RetirementsWorkFlow.doESM(requester, instance, wfDisclaimerData, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String doRetirementsSendBackUnitsESM() throws BusinessException {
	try {
	    RetirementsWorkFlow.doESM(requester, instance, wfDisclaimerData, currentTask, WFActionFlagsEnum.SEND_BACK_TO_UNITS.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public void getSentBackUnitsNames() {
	try {
	    selectedSentBackUnitsNamesString = RetirementsWorkFlow.getSentBackUnitsNames(wfDisclaimerData.getSentBackUnitsString());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void clearSentBackUnits() {
	selectedSentBackUnitsNamesString = "";
	wfDisclaimerData.setSentBackUnitsString("");
    }

    public String closeProcess() {
	try {
	    RetirementsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public void print() {
	try {
	    DisclaimerTransactionData disclaimerTransactionData = RetirementsService.getDisclaimerTransByTerminationTransId(wfDisclaimerData.getTerminationTransactionId());
	    byte[] reportBytes = RetirementsService.getDisclaimerDecisionBytes(disclaimerTransactionData.getId());
	    this.print(reportBytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public boolean isAdmin() {
	return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
	this.isAdmin = isAdmin;
    }

    public boolean isManager() {
	return isManager;
    }

    public void setManager(boolean isManager) {
	this.isManager = isManager;
    }

    public boolean isPayrollReviewer() {
	return isPayrollReviewer;
    }

    public void setPayrollReviewer(boolean isPayrollReviewer) {
	this.isPayrollReviewer = isPayrollReviewer;
    }

    public Long getReviewerEmpId() {
	return reviewerEmpId;
    }

    public void setReviewerEmpId(Long reviewerEmpId) {
	this.reviewerEmpId = reviewerEmpId;
    }

    public String getHkeyReviewerEmps() {
	return hkeyReviewerEmps;
    }

    public void setHkeyReviewerEmps(String hkeyReviewerEmps) {
	this.hkeyReviewerEmps = hkeyReviewerEmps;
    }

    public WFDisclaimerData getWfDisclaimerData() {
	return wfDisclaimerData;
    }

    public void setWfDisclaimerData(WFDisclaimerData wfDisclaimerData) {
	this.wfDisclaimerData = wfDisclaimerData;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public WFDisclaimerDetail getWfDisclaimerDetail() {
	return wfDisclaimerDetail;
    }

    public void setWfDisclaimerDetail(WFDisclaimerDetail wfDisclaimerDetail) {
	this.wfDisclaimerDetail = wfDisclaimerDetail;
    }

    public String getSentBackUnitsIdsParam() {
	return sentBackUnitsIdsParam;
    }

    public void setSentBackUnitsIdsParam(String unitsIdsString) {
	this.sentBackUnitsIdsParam = unitsIdsString;
    }

    public String getSelectedSentBackUnitsNamesString() {
	return selectedSentBackUnitsNamesString;
    }

    public void setSelectedSentBackUnitsNamesString(String selectedSentBackUnitsNamesString) {
	this.selectedSentBackUnitsNamesString = selectedSentBackUnitsNamesString;
    }

}
