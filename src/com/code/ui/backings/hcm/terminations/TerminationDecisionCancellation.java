package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationCancellationMovementData;
import com.code.enums.CategoryModesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.TerminationsWorkflow;
import com.code.ui.backings.base.WFBaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "terminationDecisionCancellation")
@ViewScoped
public class TerminationDecisionCancellation extends WFBaseBacking {
    /**
     * 2 for Soldiers and 3 for Civil
     **/
    private int mode;

    // For Search
    private EmployeeData selectedEmpData;
    private Long selectedEmpId;
    private Long selectedEmpRankId;

    private WFTerminationCancellationMovementData wfCancellationMovementData;
    private TerminationTransactionData cancelledTerminationTransactionData;

    private List<EmployeeData> internalCopies;

    public TerminationDecisionCancellation() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		setScreenTitle(getMessage("title_soldiersTerminationDecisionCancellation"));
		this.processId = WFProcessesEnum.SOLDIERS_TERMINATION_CANCELLATION.getCode();
	    } else if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
		setScreenTitle(getMessage("title_civiliansTerminationDecisionCancellation"));
		this.processId = WFProcessesEnum.CIVILIANS_TERMINATION_CANCELLATION.getCode();
	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_URLError"));
	}

	this.initialize();
    }

    public void initialize() {
	try {
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfCancellationMovementData = TerminationsWorkflow.getWFTerminationCancellationMovementByInstanceId(instance.getInstanceId());
		selectedEmpData = EmployeesService.getEmployeeData(wfCancellationMovementData.getEmpId());
		selectedEmpId = selectedEmpData.getEmpId();
		selectedEmpRankId = selectedEmpData.getRankId();
		// cancelledTerminationTransactionData = TerminationsService.getTerminationTransactionForCancellation(wfCancellationMovementData.getEmpId());
		cancelledTerminationTransactionData = TerminationsService.getTerminationTransactionById(wfCancellationMovementData.getCancelTransactionId());
		internalCopies = EmployeesService.getEmployeesByIdsString(wfCancellationMovementData.getInternalCopies());
	    } else {
		reset();
		internalCopies = new ArrayList<EmployeeData>();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void reset() {
	wfCancellationMovementData = new WFTerminationCancellationMovementData();
	cancelledTerminationTransactionData = new TerminationTransactionData();
	selectedEmpData = new EmployeeData();
	selectedEmpId = null;
	selectedEmpRankId = (long) FlagsEnum.ALL.getCode();
    }

    public void constructEmpData() {
	try {
	    if (selectedEmpId != null && selectedEmpId != FlagsEnum.OFF.getCode()) {
		selectedEmpData = EmployeesService.getEmployeeData(selectedEmpId);
		selectedEmpRankId = selectedEmpData.getRankId();
		cancelledTerminationTransactionData = TerminationsWorkflow.addTerminationDecisionCancellationEmployee(requester, selectedEmpData, wfCancellationMovementData, mode);
	    }
	} catch (BusinessException e) {
	    reset();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public boolean disableChangeJobFlag() {
	if (selectedEmpData == null || selectedEmpData.getStatusId().equals(EmployeeStatusEnum.SERVICE_TERMINATED.getCode()))
	    return false;

	return true;
    }

    public String initProcess() {
	String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	wfCancellationMovementData.setInternalCopies(internalCopiesString);
	try {
	    TerminationsWorkflow.initTerminationCancellationMovement(wfCancellationMovementData, null, requester, processId, attachments, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveRE() {
	String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	wfCancellationMovementData.setInternalCopies(internalCopiesString);
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementRE(requester, instance, wfCancellationMovementData, null, currentTask, attachments, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by reviewer employee
    public String rejectRE() {
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementRE(requester, instance, wfCancellationMovementData, null, currentTask, attachments, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String signSM() {
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementSM(requester, instance, wfCancellationMovementData, cancelledTerminationTransactionData.getReasonId(), currentTask, loginEmpData.getEmpId(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION_CANCELLATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), cancelledTerminationTransactionData.getCategoryId(), WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectSM() {
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementSM(requester, instance, wfCancellationMovementData, cancelledTerminationTransactionData.getReasonId(), currentTask, loginEmpData.getEmpId(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION_CANCELLATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), cancelledTerminationTransactionData.getCategoryId(), WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifySM() {
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementSM(requester, instance, wfCancellationMovementData, cancelledTerminationTransactionData.getReasonId(), currentTask, loginEmpData.getEmpId(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION_CANCELLATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), cancelledTerminationTransactionData.getCategoryId(), WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public void print() {
	try {
	    byte[] bytes = TerminationsService.getCancellationTerminationDecisionBytes(wfCancellationMovementData.getCancelTransactionId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String closeProcess() {
	try {
	    TerminationsWorkflow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // Setters and Getters
    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public EmployeeData getSelectedEmpData() {
	return selectedEmpData;
    }

    public void setSelectedEmpData(EmployeeData selectedEmpData) {
	this.selectedEmpData = selectedEmpData;
    }

    public Long getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(Long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

    public Long getSelectedEmpRankId() {
	return selectedEmpRankId;
    }

    public void setSelectedEmpRankId(Long selectedEmpRankId) {
	this.selectedEmpRankId = selectedEmpRankId;
    }

    public WFTerminationCancellationMovementData getWfCancellationMovementData() {
	return wfCancellationMovementData;
    }

    public void setWfCancellationMovementData(WFTerminationCancellationMovementData wfCancellationMovementData) {
	this.wfCancellationMovementData = wfCancellationMovementData;
    }

    public TerminationTransactionData getCancelledTerminationTransactionData() {
	return cancelledTerminationTransactionData;
    }

    public void setCancelledTerminationTransactionData(TerminationTransactionData cancelledTerminationTransactionData) {
	this.cancelledTerminationTransactionData = cancelledTerminationTransactionData;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

}
