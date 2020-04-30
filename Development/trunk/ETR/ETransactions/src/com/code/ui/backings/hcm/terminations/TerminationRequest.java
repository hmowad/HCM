package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.terminations.TerminationRecordData;
import com.code.dal.orm.hcm.terminations.TerminationRecordDetailData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationData;
import com.code.enums.CategoriesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.TerminationRequestTypeEnum;
import com.code.enums.TerminationTypeFlagEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TerminationsWorkflow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "terminationRequest")
@ViewScoped
public class TerminationRequest extends WFBaseBacking {

    /**
     * 1 for Officers, 2 for Soldiers and 3 for Civil
     **/
    private int mode;
    private List<TerminationTransactionData> terminationExtensionTransactionDataList;
    private WFTerminationData wfTermination;
    private TerminationRecordData terminationRecordData;
    private TerminationRecordDetailData terminationRecordDetailData;
    private List<TerminationRecordDetailData> terminationRecordDetailDataList; // used as wrapper for the single detail, as WorkFlow methods receive a list

    private List<EmployeeData> internalCopies;
    private List<EmployeeData> reviewerEmps;
    private Long selectedReviewerEmpId;

    private Boolean isDisabledTerminationRequestType;
    private final int pageSize = 10;

    public TerminationRequest() {
	super.init();

	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    if (mode == CategoriesEnum.OFFICERS.getCode()) {
		this.processId = WFProcessesEnum.OFFICERS_TERMINATION_REQUEST.getCode();
		setScreenTitle(getMessage("title_officersTerminationRequest"));
	    } else if (mode == CategoriesEnum.SOLDIERS.getCode()) {
		this.processId = WFProcessesEnum.SOLDIERS_TERMINATION_REQUEST.getCode();
		setScreenTitle(getMessage("title_soldiersTerminationRequest"));
	    } else if (mode == CategoriesEnum.PERSONS.getCode()) {
		this.processId = WFProcessesEnum.CIVILIANS_TERMINATION_REQUEST.getCode();
		setScreenTitle(getMessage("title_personsTerminationRequest"));
	    } else if (mode == CategoriesEnum.CONTRACTORS.getCode()) {
		this.processId = WFProcessesEnum.CONTRACTORS_TERMINATION_REQUEST.getCode();
		setScreenTitle(getMessage("title_contractorsTerminationRequest"));
	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_URLError"));
	}
	init();
    }

    public void init() {
	try {
	    terminationExtensionTransactionDataList = TerminationsService.getTerminationExtensionTransactionsByEmpId(requester.getEmpId());
	    terminationRecordData = new TerminationRecordData();
	    terminationRecordData.setTypeFlag(TerminationTypeFlagEnum.REQUEST.getCode());
	    terminationRecordDetailData = new TerminationRecordDetailData();
	    terminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
	    internalCopies = new ArrayList<EmployeeData>();

	    isDisabledTerminationRequestType = false;

	    // in case of RE get record and details using wfTermination recordId
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfTermination = new WFTerminationData();
	    } else {
		wfTermination = TerminationsWorkflow.getWFTerminationByInstanceId(instance.getInstanceId());
		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		}

		if (this.role.equals(WFTaskRolesEnum.DIRECT_MANAGER.getCode()) || wfTermination.getRecordId() == null)
		    return;

		terminationRecordData = TerminationsService.getTerminationRecordDataById(wfTermination.getRecordId());
		terminationRecordDetailData = TerminationsService.getTerminationRecordDetailsByRecordId(wfTermination.getRecordId()).get(0);

		terminationRecordDetailDataList.add(terminationRecordDetailData);
		internalCopies = EmployeesService.getEmployeesByIdsString(terminationRecordData.getInternalCopies());

		if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {
		    // in case of officers, desiredTerminationDate can be null
		    float servicePeriodYears = HijriDateService.hijriDateDiffByHijriYear(requester.getRecruitmentDate(), wfTermination.getDesiredTerminationDate());

		    if (mode == CategoriesEnum.SOLDIERS.getCode()) {
			if (servicePeriodYears < ETRConfigurationService.getTerminationsSoldiersServicePeriodRetirementYearsMin()) {
			    terminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RESIGNATION.getCode());
			    isDisabledTerminationRequestType = true;
			} else if (terminationRecordDetailData.getTerminationRequestType() == null)
			    terminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RETIREMENT.getCode());
		    } else if (mode == CategoriesEnum.PERSONS.getCode()) {
			if (servicePeriodYears < ETRConfigurationService.getTerminationsCiviliansServicePeriodRetirementYearsMin()) {
			    terminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RESIGNATION.getCode());
			    isDisabledTerminationRequestType = true;
			} else if (terminationRecordDetailData.getTerminationRequestType() == null)
			    terminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RETIREMENT.getCode());
		    }
		} else if (role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()) && mode == CategoriesEnum.OFFICERS.getCode()) {
		    String prevNotifyTaskAction = prevTasks.get(prevTasks.size() - 1).getAction();
		    if (prevNotifyTaskAction.equals(WFTaskActionsEnum.APPROVE.getCode()))
			notificationMessage = super.getMessage("notify_terminationRequestApproved");
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // initialize process by requester
    public String initProcess() {
	// 1. in this case (request) we do NOT have any details yet... is it okay to send an empty list ? or null ?
	try {
	    TerminationsWorkflow.initTermination(requester, wfTermination, terminationRecordData, null, processId, attachments, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve by direct manager
    public String approveDM() {
	try {
	    TerminationsWorkflow.doTerminationDM(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by direct manager
    public String rejectDM() {
	try {
	    TerminationsWorkflow.doTerminationDM(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // SMR
    public String redirectSMR() {
	try {
	    TerminationsWorkflow.doTerminationSMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approveSRE
    public String approveSRE() {

	try {
	    TerminationsWorkflow.doTerminationSRE(requester, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // RejectSRE
    public String rejectSRE() {
	try {
	    TerminationsWorkflow.doTerminationSRE(requester, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // SSM
    public String signSSM() {
	try {
	    TerminationsWorkflow.doTerminationSSM(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // modifySSM
    public String modifySSM() {
	try {
	    TerminationsWorkflow.doTerminationSSM(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // rejectSSM
    public String rejectSSM() {
	try {
	    TerminationsWorkflow.doTerminationSSM(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String redirectMR() {
	try {
	    TerminationsWorkflow.doTerminationMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve by reviewer employee
    public String approveRE() {
	String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	terminationRecordData.setInternalCopies(internalCopiesString);
	try {
	    TerminationsWorkflow.doTerminationRE(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, attachments, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by reviewer employee
    public String rejectRE() {
	try {
	    TerminationsWorkflow.doTerminationRE(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, attachments, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String signSM() {
	try {
	    TerminationsWorkflow.doTerminationSM(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifySM() {
	try {
	    TerminationsWorkflow.doTerminationSM(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectSM() {
	try {
	    TerminationsWorkflow.doTerminationSM(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, currentTask, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // close process
    public String closeProcess() {
	try {
	    TerminationsWorkflow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public void print() {
	try {
	    TerminationTransactionData terminationTransactionData = TerminationsService.getTerminationTransactionByRecordDetailId(terminationRecordDetailData.getId());
	    if (terminationTransactionData != null) {
		byte[] bytes = TerminationsService.getTerminationDecisionBytes(terminationTransactionData.getId(), mode, terminationTransactionData.getTransactionTypeCode());
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /*****************************************************************************************************************/
    // Getters and Setters
    public int getMode() {
	return mode;
    }

    public List<TerminationTransactionData> getTerminationExtensionTransactionDataList() {
	return terminationExtensionTransactionDataList;
    }

    public void setTerminationExtensionTransactionDataList(List<TerminationTransactionData> terminationExtensionTransactionDataList) {
	this.terminationExtensionTransactionDataList = terminationExtensionTransactionDataList;
    }

    public WFTerminationData getWfTermination() {
	return wfTermination;
    }

    public void setWfTermination(WFTerminationData wfTermination) {
	this.wfTermination = wfTermination;
    }

    public TerminationRecordData getTerminationRecordData() {
	return terminationRecordData;
    }

    public void setTerminationRecordData(TerminationRecordData terminationRecordData) {
	this.terminationRecordData = terminationRecordData;
    }

    public TerminationRecordDetailData getTerminationRecordDetailData() {
	return terminationRecordDetailData;
    }

    public void setTerminationRecordDetailData(TerminationRecordDetailData terminationRecordDetailData) {
	this.terminationRecordDetailData = terminationRecordDetailData;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

    public List<EmployeeData> getReviewerEmps() {
	return reviewerEmps;
    }

    public void setReviewerEmps(List<EmployeeData> reviewerEmps) {
	this.reviewerEmps = reviewerEmps;
    }

    public Long getSelectedReviewerEmpId() {
	return selectedReviewerEmpId;
    }

    public void setSelectedReviewerEmpId(Long selectedReviewerEmpId) {
	this.selectedReviewerEmpId = selectedReviewerEmpId;
    }

    public Boolean getIsDisabledTerminationRequestType() {
	return isDisabledTerminationRequestType;
    }

    public String getTerminationRequestCondition() {

	if (mode == CategoriesEnum.OFFICERS.getCode())
	    return getParameterizedMessage("label_terminationRequestOfficersCondition", new Object[] { ETRConfigurationService.getTerminationsOfficersServicePeriodRetirementYearsMin(), ETRConfigurationService.getTerminationsOfficersServicePeriodRetirementYearsMax(), ETRConfigurationService.getTerminationsOfficersPeriodUntilDesiredTerminationDateDaysMax() });
	else if (mode == CategoriesEnum.SOLDIERS.getCode())
	    return getParameterizedMessage("label_terminationRequestSoldiersCondition", new Object[] { ETRConfigurationService.getTerminationsSoldiersServicePeriodRetirementYearsMin(), ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMin(), ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMax() });
	else
	    return getParameterizedMessage("label_terminationRequestCiviliansCondition", new Object[] { ETRConfigurationService.getTerminationsCiviliansServicePeriodRetirementYearsMin() });

    }

    public int getPageSize() {
	return pageSize;
    }

}
